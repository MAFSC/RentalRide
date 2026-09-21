// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

/// @title CarBridge — мост между Java-бэкендом и Stylus-контрактом car_ranker
/// @notice Хранит данные о машинах и делегирует ранжирование в Stylus (Rust/WASM)
contract CarBridge {

    /// @notice Структура данных автомобиля (8 критериев из ТЗ + цена)
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

    /// @notice Адрес Stylus-контракта car_ranker (изменяемый, чтобы можно было обновлять без передеплоя моста)
    address public stylusRanker;

    /// @notice Владелец контракта (для добавления машин и обновления адреса Stylus)
    address public owner;

    /// @notice Счётчик машин и маппинг ID → данные
    uint256 public carCount;
    mapping(uint256 => CarData) public cars;
    mapping(uint256 => bool) public carExists;

    /// @notice События для индексации бэкендом
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
    /// @dev    Вызывается владельцем. Не требует передеплоя самого CarBridge.
    function updateStylusAddress(address _newStylusRanker) external onlyOwner {
        require(_newStylusRanker != address(0), "CarBridge: zero stylus address");
        address old = stylusRanker;
        stylusRanker = _newStylusRanker;
        emit StylusAddressUpdated(old, _newStylusRanker);
    }

    // ─────────────────────────────────────────────────────────────
    //  Управление данными (Web2 → Blockchain)
    // ─────────────────────────────────────────────────────────────

    /// @notice Добавить машину. Вызывается Spring Boot бэкендом после загрузки фото в IPFS.
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

    /// @notice Обновить данные машины (например, после ТО или нового отзыва)
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

    /// @notice Пометить машину как недоступную (снята с аренды)
    function removeCar(uint256 carId) external onlyOwner {
        require(carExists[carId], "CarBridge: car does not exist");
        carExists[carId] = false;
        emit CarRemoved(carId);
    }

    // ─────────────────────────────────────────────────────────────
    //  Ранжирование через Stylus
    // ─────────────────────────────────────────────────────────────

    /// @notice Найти лучшую машину по заданным весам.
    /// @dev    Формирует плоский массив [price, mileage, power, rating, ...] и вызывает Stylus.
    /// @param  carIds         Массив ID машин-кандидатов (формируется бэкендом)
    /// @param  weightPrice    Вес цены (чем больше, тем важнее низкая цена)
    /// @param  weightMileage  Вес пробега (чем больше, тем важнее малый пробег)
    /// @param  weightPower    Вес мощности (чем больше, тем важнее мощность)
    /// @param  weightRating   Вес отзывов (чем больше, тем важнее высокий рейтинг)
    /// @return bestCarId      ID лучшей машины
    /// @return score          Итоговый скор (для отладки/логов)
    function findBestCar(
        uint256[] calldata carIds,
        uint256 weightPrice,
        uint256 weightMileage,
        uint256 weightPower,
        uint256 weightRating
    ) external view returns (uint256 bestCarId, uint256 score) {
        require(carIds.length > 0, "CarBridge: empty carIds");

        // Сначала считаем, сколько машин реально существует, чтобы не тратить газ на несуществующие
        uint256 validCount = 0;
        for (uint256 i = 0; i < carIds.length; i++) {
            if (carExists[carIds[i]]) validCount++;
        }
        require(validCount > 0, "CarBridge: no valid cars");

        // Формируем плоский массив и массив реальных ID
        uint256[] memory flat = new uint256[](validCount * 4);
        uint256[] memory validIds = new uint256[](validCount);

        uint256 j = 0;
        for (uint256 i = 0; i < carIds.length; i++) {
            if (!carExists[carIds[i]]) continue;
            CarData memory c = cars[carIds[i]];
            flat[j * 4 + 0] = c.pricePerDay;
            flat[j * 4 + 1] = c.mileage;
            flat[j * 4 + 2] = c.enginePower;
            flat[j * 4 + 3] = c.rating;
            validIds[j] = carIds[i];
            j++;
        }

        // Кодируем вызов Stylus-контракта
        bytes memory callData = abi.encodeWithSignature(
            "findBestCar(uint256[],uint256,uint256,uint256,uint256)",
            flat,
            weightPrice,
            weightMileage,
            weightPower,
            weightRating
        );

        (bool ok, bytes memory result) = stylusRanker.staticcall(callData);
        require(ok, "CarBridge: stylus call failed");
        require(result.length >= 64, "CarBridge: bad stylus response");

        // Декодируем два uint256: индекс и скор
        (uint256 bestIndex, uint256 finalScore) = abi.decode(result, (uint256, uint256));

        // Stylus возвращает индекс в плоском массиве. Преобразуем в реальный carId.
        require(bestIndex < validCount, "CarBridge: bad index from stylus");
        bestCarId = validIds[bestIndex];
        score = finalScore;
    }

    /// @notice View-функция для получения полных данных о машине (для Java-бэкенда)
    function getCar(uint256 carId) external view returns (CarData memory) {
        require(carExists[carId], "CarBridge: car does not exist");
        return cars[carId];
    }

    /// @notice View-функция для получения всех доступных машин (для фильтрации на бэкенде)
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
