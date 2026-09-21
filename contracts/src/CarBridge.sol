// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

/// @title CarBridge — мост между Java-бэкендом и Stylus-контрактом car_ranker
/// @notice Хранит данные о машинах и делегирует ранжирование в Stylus (Rust/WASM)
/// @dev    Версия 3: 7 критериев ранжирования (цена, пробег, мощность, рейтинг, год, салон, топливо)
contract CarBridge {

    /// @notice Структура данных автомобиля (все критерии из ТЗ + цена)
    struct CarData {
        string  brand;          // Марка
        uint256 mileage;        // Пробег (в км)
        uint256 enginePower;    // Мощность двигателя (в л.с.)
        string  fuelType;       // Вид топлива ("petrol", "diesel", "electric", "hybrid")
        uint256 year;           // Год выпуска
        uint256 interiorScore;  // Состояние салона (0-100)
        uint256 rating;         // Отзывы тех, кто уже брал (0-500, т.е. 0.0-5.0)
        uint256 pricePerDay;    // Цена аренды за день (в USDC, 6 знаков)
    }

    /// @notice Адрес Stylus-контракта car_ranker
    address public stylusRanker;

    /// @notice Владелец контракта
    address public owner;

    /// @notice Счётчик машин и маппинг ID → данные
    uint256 public carCount;
    mapping(uint256 => CarData) public cars;
    mapping(uint256 => bool) public carExists;

    /// @notice События
    event CarAdded(uint256 indexed carId, string brand, uint256 pricePerDay);
    event CarUpdated(uint256 indexed carId);
    event CarRemoved(uint256 indexed carId);
    event StylusAddressUpdated(address indexed oldAddress, address indexed newAddress);

    modifier onlyOwner() {
        require(msg.sender == owner, "CarBridge: not owner");
        _;
    }

    constructor(address _stylusRanker) {
        require(_stylusRanker != address(0), "CarBridge: zero stylus address");
        stylusRanker = _stylusRanker;
        owner = msg.sender;
    }

    // ─────────────────────────────────────────────────────────────
    //  Управление адресом Stylus
    // ─────────────────────────────────────────────────────────────

    /// @notice Обновить адрес Stylus-контракта (после передеплоя car_ranker)
    function updateStylusAddress(address _newStylusRanker) external onlyOwner {
        require(_newStylusRanker != address(0), "CarBridge: zero stylus address");
        address old = stylusRanker;
        stylusRanker = _newStylusRanker;
        emit StylusAddressUpdated(old, _newStylusRanker);
    }

    // ─────────────────────────────────────────────────────────────
    //  Управление данными
    // ─────────────────────────────────────────────────────────────

    /// @notice Добавить машину
    function addCar(
        string calldata brand,
        uint256 mileage,
        uint256 enginePower,
        string calldata fuelType,
        uint256 year,
        uint256 interiorScore,
        uint256 rating,
        uint256 pricePerDay
    ) external onlyOwner returns (uint256 carId) {
        require(interiorScore <= 100, "CarBridge: interiorScore > 100");
        require(rating <= 500, "CarBridge: rating > 500");
        require(year >= 1900 && year <= 2100, "CarBridge: bad year");

        carId = carCount;
        cars[carId] = CarData({
            brand: brand,
            mileage: mileage,
            enginePower: enginePower,
            fuelType: fuelType,
            year: year,
            interiorScore: interiorScore,
            rating: rating,
            pricePerDay: pricePerDay
        });
        carExists[carId] = true;
        carCount++;

        emit CarAdded(carId, brand, pricePerDay);
    }

    /// @notice Обновить данные машины
    function updateCar(
        uint256 carId,
        uint256 mileage,
        uint256 interiorScore,
        uint256 rating,
        uint256 pricePerDay
    ) external onlyOwner {
        require(carExists[carId], "CarBridge: car does not exist");

        CarData storage car = cars[carId];
        car.mileage = mileage;
        car.interiorScore = interiorScore;
        car.rating = rating;
        car.pricePerDay = pricePerDay;

        emit CarUpdated(carId);
    }

    /// @notice Пометить машину как недоступную
    function removeCar(uint256 carId) external onlyOwner {
        require(carExists[carId], "CarBridge: car does not exist");
        carExists[carId] = false;
        emit CarRemoved(carId);
    }

    // ─────────────────────────────────────────────────────────────
    //  Вспомогательные функции
    // ─────────────────────────────────────────────────────────────

    /// @notice Конвертирует строку топлива в нормализованный скор (0..1e6)
    /// @dev    electric = 1.0, hybrid = 0.75, petrol = 0.5, diesel = 0.25, unknown = 0.1
    function _fuelScore(string memory fuelType) internal pure returns (uint256) {
        bytes32 h = keccak256(bytes(fuelType));
        if (h == keccak256(bytes("electric"))) return 1_000_000;
        if (h == keccak256(bytes("hybrid")))   return 750_000;
        if (h == keccak256(bytes("petrol")))   return 500_000;
        if (h == keccak256(bytes("diesel")))   return 250_000;
        return 100_000; // unknown
    }

    // ─────────────────────────────────────────────────────────────
    //  Ранжирование через Stylus (7 критериев)
    // ─────────────────────────────────────────────────────────────

    /// @notice Найти лучшую машину по заданным весам (7 критериев).
    /// @dev    Формирует плоский массив [price, mileage, power, rating, year, interior, fuelScore, 0] на каждую машину.
    /// @param  carIds          Массив ID машин-кандидатов
    /// @param  weightPrice     Вес цены
    /// @param  weightMileage   Вес пробега
    /// @param  weightPower     Вес мощности
    /// @param  weightRating    Вес рейтинга
    /// @param  weightYear      Вес года выпуска
    /// @param  weightInterior  Вес состояния салона
    /// @param  weightFuel      Вес типа топлива
    /// @return bestCarId       ID лучшей машины
    /// @return score           Итоговый скор
    function findBestCar(
        uint256[] calldata carIds,
        uint256 weightPrice,
        uint256 weightMileage,
        uint256 weightPower,
        uint256 weightRating,
        uint256 weightYear,
        uint256 weightInterior,
        uint256 weightFuel
    ) external view returns (uint256 bestCarId, uint256 score) {
        require(carIds.length > 0, "CarBridge: empty carIds");

        // Считаем валидные машины
        uint256 validCount = 0;
        for (uint256 i = 0; i < carIds.length; i++) {
            if (carExists[carIds[i]]) validCount++;
        }
        require(validCount > 0, "CarBridge: no valid cars");

        // Плоский массив: 8 значений на машину
        uint256[] memory flat = new uint256[](validCount * 8);
        uint256[] memory validIds = new uint256[](validCount);

        uint256 j = 0;
        for (uint256 i = 0; i < carIds.length; i++) {
            if (!carExists[carIds[i]]) continue;
            CarData memory c = cars[carIds[i]];
            flat[j * 8 + 0] = c.pricePerDay;
            flat[j * 8 + 1] = c.mileage;
            flat[j * 8 + 2] = c.enginePower;
            flat[j * 8 + 3] = c.rating;
            flat[j * 8 + 4] = c.year;
            flat[j * 8 + 5] = c.interiorScore;
            flat[j * 8 + 6] = _fuelScore(c.fuelType);
            flat[j * 8 + 7] = 0; // reserved
            validIds[j] = carIds[i];
            j++;
        }

        // Кодируем вызов Stylus (7 весов)
        bytes memory callData = abi.encodeWithSignature(
            "findBestCar(uint256[],uint256,uint256,uint256,uint256,uint256,uint256,uint256)",
            flat,
            weightPrice,
            weightMileage,
            weightPower,
            weightRating,
            weightYear,
            weightInterior,
            weightFuel
        );

        (bool ok, bytes memory result) = stylusRanker.staticcall(callData);
        require(ok, "CarBridge: stylus call failed");
        require(result.length >= 64, "CarBridge: bad stylus response");

        (uint256 bestIndex, uint256 finalScore) = abi.decode(result, (uint256, uint256));
        require(bestIndex < validCount, "CarBridge: bad index from stylus");
        bestCarId = validIds[bestIndex];
        score = finalScore;
    }

    // ─────────────────────────────────────────────────────────────
    //  View-функции для Java-бэкенда
    // ─────────────────────────────────────────────────────────────

    function getCar(uint256 carId) external view returns (CarData memory) {
        require(carExists[carId], "CarBridge: car does not exist");
        return cars[carId];
    }

    function getAllAvailableCars() external view returns (uint256[] memory ids) {
        uint256 count = 0;
        for (uint256 i = 0; i < carCount; i++) {
            if (carExists[i]) count++;
        }
        ids = new uint256[](count);
        uint256 j = 0;
        for (uint256 i = 0; i < carCount; i++) {
            if (carExists[i]) {
                ids[j] = i;
                j++;
            }
        }
    }
}
