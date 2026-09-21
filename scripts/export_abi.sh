#!/bin/bash
# Скрипт для экспорта ABI из Stylus-контракта для интеграции с Java
cd ../stylus/car_ranker
cargo stylus export-abi --json > ../../contracts/abi/CarRanker.json
