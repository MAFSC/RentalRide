#!/bin/bash
# Скрипт для деплоя Stylus-контракта в тестовую сеть Arbitrum Sepolia
# Перед запуском убедитесь, что у вас установлен cargo-stylus и настроен кошелек

cd ../stylus/car_ranker
echo "🚀 Проверка Stylus-контракта..."
cargo stylus check --endpoint https://sepolia-rollup.arbitrum.io/rpc

echo "📦 Деплой Stylus-контракта..."
cargo stylus deploy \
  --endpoint https://sepolia-rollup.arbitrum.io/rpc \
  --private-key-path ../../.private_key
