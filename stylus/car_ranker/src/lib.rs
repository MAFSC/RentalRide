// SPDX-License-Identifier: MIT
#![cfg_attr(not(any(feature = "export-abi", test)), no_main)]
extern crate alloc;

use alloc::vec::Vec;
use stylus_sdk::{
    prelude::*,
    alloy_primitives::{Address, U256},
};

sol_interface! {
    // Здесь мы объявляем интерфейс к Solidity-контракту (мосту),
    // который будет передавать нам данные о машинах.
    interface ICarBridge {
        function getCarData(uint256 carId) external view returns (
            uint256 price,
            uint256 mileage,
            uint256 enginePower,
            uint256 year,
            uint256 rating
        );
    }
}

sol_storage! {
    #[entrypoint]
    pub struct CarRanker {
        // В этой структуре мы будем хранить адрес Solidity-моста
        address bridge;
    }
}

#[public]
impl CarRanker {
    /// Устанавливает адрес Solidity-моста (вызывается один раз при деплое)
    pub fn set_bridge(&mut self, bridge_address: Address) -> Result<(), Vec<u8>> {
        self.bridge.set(bridge_address);
        Ok(())
    }

    /// Основная функция: находит лучшую машину по заданным весам.
    /// В Rust мы делаем всю тяжелую математику, которая была бы дорогой в Solidity.
    pub fn find_best_car(
        &self,
        car_ids: Vec<U256>,
        weight_price: U256,
        weight_mileage: U256,
        weight_power: U256,
    ) -> Result<U256, Vec<u8>> {
        let bridge_addr = self.bridge.get();
        let bridge = ICarBridge::new(bridge_addr);
        let config = Call::new();

        let mut best_score = U256::ZERO;
        let mut best_car_id = U256::ZERO;

        // Перебираем машины, получаем их данные от моста и считаем скор.
        for car_id in car_ids {
            // Вызываем Solidity-контракт, чтобы получить данные конкретной машины
            let (price, mileage, power, _year, _rating) =
                bridge.get_car_data(self.vm(), config, car_id).unwrap();

            // Считаем "ценность": чем меньше пробег и цена, тем лучше.
            // (Упрощенная формула для примера)
            let score = (weight_price * (U256::from(1_000_000) / (price + 1))) 
                      + (weight_mileage * (U256::from(100_000) / (mileage + 1)))
                      + (weight_power * power);

            if score > best_score {
                best_score = score;
                best_car_id = car_id;
            }
        }
        Ok(best_car_id)
    }
}
