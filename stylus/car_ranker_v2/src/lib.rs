// SPDX-License-Identifier: MIT
#![cfg_attr(not(any(feature = "export-abi", test)), no_main)]
extern crate alloc;

use alloc::vec::Vec;
use stylus_sdk::{
    prelude::*,
    alloy_primitives::U256,
};

sol_storage! {
    #[entrypoint]
    pub struct CarRanker {}
}

#[public]
impl CarRanker {
    /// @notice Находит лучшую машину по заданным весам.
    /// @dev    Принимает плоский массив по 8 значений на машину:
    ///         [price, mileage, power, rating, year, interiorScore, fuelScore, reserved]
    ///         Возвращает (индекс лучшей машины в плоском массиве, её итоговый скор).
    ///
    ///         Формула скора: чем ВЫШЕ — тем лучше.
    ///         - Цена: чем МЕНЬШЕ, тем лучше → инвертируем
    ///         - Пробег: чем МЕНЬШЕ, тем лучше → инвертируем
    ///         - Мощность: чем БОЛЬШЕ, тем лучше
    ///         - Рейтинг: чем БОЛЬШЕ, тем лучше
    ///         - Год: чем НОВЕЕ, тем лучше
    ///         - Состояние салона: чем БОЛЬШЕ, тем лучше
    ///         - Топливо: electric=1e6, hybrid=750k, petrol=500k, diesel=250k
    pub fn find_best_car(
        &self,
        flat: Vec<U256>,
        weight_price: U256,
        weight_mileage: U256,
        weight_power: U256,
        weight_rating: U256,
        weight_year: U256,
        weight_interior: U256,
        weight_fuel: U256,
    ) -> Result<(U256, U256), Vec<u8>> {
        // Проверяем, что массив корректен: 8 значений на машину
        let total = flat.len();
        if total == 0 || total % 8 != 0 {
            return Err(b"CarRanker: invalid flat array length".to_vec());
        }

        let car_count = total / 8;
        let mut best_index = U256::ZERO;
        let mut best_score = U256::ZERO;

        // Константы нормализации (масштабируем до 1e6)
        let scale = U256::from(1_000_000u64);
        let max_mileage = U256::from(1_000_000u64); // 1 млн км — верхняя граница
        let max_power = U256::from(2_000u64);       // 2000 л.с. — верхняя граница
        let max_rating = U256::from(500u64);        // 5.0 в формате 0-500
        let min_year = U256::from(1990u64);         // нижняя граница года
        let max_year = U256::from(2030u64);         // верхняя граница года
        let max_interior = U256::from(100u64);      // состояние салона 0-100

        for i in 0..car_count {
            let base = i * 8;
            let price = flat[base];
            let mileage = flat[base + 1];
            let power = flat[base + 2];
            let rating = flat[base + 3];
            let year = flat[base + 4];
            let interior = flat[base + 5];
            let fuel_score = flat[base + 6];
            // flat[base + 7] — reserved, не используется

            // ─── Цена: инвертируем (чем меньше, тем лучше) ───
            // Защита от деления на 0
            let price_score = if price == U256::ZERO {
                scale
            } else {
                scale / (price + U256::from(1u64))
            };

            // ─── Пробег: инвертируем (чем меньше, тем лучше) ───
            let mileage_score = if mileage >= max_mileage {
                U256::ZERO
            } else {
                scale - (mileage * scale / max_mileage)
            };

            // ─── Мощность: чем больше, тем лучше ───
            let power_score = if power >= max_power {
                scale
            } else {
                power * scale / max_power
            };

            // ─── Рейтинг: чем больше, тем лучше ───
            let rating_score = if rating >= max_rating {
                scale
            } else {
                rating * scale / max_rating
            };

            // ─── Год выпуска: чем новее, тем лучше ───
            let year_score = if year <= min_year {
                U256::ZERO
            } else if year >= max_year {
                scale
            } else {
                (year - min_year) * scale / (max_year - min_year)
            };

            // ─── Состояние салона: чем выше, тем лучше ───
            let interior_score = if interior >= max_interior {
                scale
            } else {
                interior * scale / max_interior
            };

            // ─── Тип топлива: уже нормализован в Solidity (0..1e6) ───
            let fuel_normalized = if fuel_score >= scale {
                scale
            } else {
                fuel_score
            };

            // ─── Итоговый скор = взвешенная сумма 7 критериев ───
            // Используем saturating_* для защиты от переполнения U256
            let score = weight_price
                .saturating_mul(price_score)
                .saturating_add(weight_mileage.saturating_mul(mileage_score))
                .saturating_add(weight_power.saturating_mul(power_score))
                .saturating_add(weight_rating.saturating_mul(rating_score))
                .saturating_add(weight_year.saturating_mul(year_score))
                .saturating_add(weight_interior.saturating_mul(interior_score))
                .saturating_add(weight_fuel.saturating_mul(fuel_normalized));

            if score > best_score {
                best_score = score;
                best_index = U256::from(i);
            }
        }

        Ok((best_index, best_score))
    }
}
