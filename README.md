# 🚗 RentalRide — Verifiable Car Aggregator

**Verifiable car rental search powered by Arbitrum Stylus (Rust) and Solidity.**

**Верифицируемый поиск автомобилей для аренды на Arbitrum Stylus (Rust) и Solidity.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Stylus](https://img.shields.io/badge/Stylus-Rust-blue)](https://docs.arbitrum.io/stylus)
[![Solidity](https://img.shields.io/badge/Solidity-0.8.23-363636)](https://soliditylang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F)](https://spring.io/projects/spring-boot)

### What is this

**RentalRide** is a decentralized aggregator for car rentals with **verifiable search**. Users set weights for 7 criteria, and a **Stylus smart contract written in Rust** computes the best car **on-chain**. Nobody — not even the platform owner — can secretly change the ranking formula or manipulate the results.

Unlike traditional aggregators (Trivago, Booking), where a central server decides what to show, here **every calculation is public and reproducible**.

### Why it matters

| Aspect | Traditional Aggregator | RentalRide |
|---|---|---|
| Who decides "the best" | Company server | Smart contract |
| Can you verify the formula | ❌ No | ✅ Yes, anyone can call it |
| Can the formula be changed secretly | ✅ Yes | ❌ No, only via public tx |
| Can prices be manipulated | ✅ Yes | ❌ No, data is on-chain |
| Can advertisers buy top spot | ✅ Yes | ❌ No, formula doesn't know ads |
| Can the result be faked | ✅ Yes | ❌ No, result is on-chain |

### Architecture
┌──────────────────────────────────────────────────────────┐
│ Frontend (HTML + JS)                                     │
│ http://localhost:8080/                                   │ 
└─────────────────────┬────────────────────────────────────┘
                      │ REST API
                      ▼
┌──────────────────────────────────────────────────────────┐
│ Spring Boot 3.2.5 (Java 21)                              │
│ CarController → CarRankingService → Web3j                │
└─────────────────────┬────────────────────────────────────┘
                      │ eth_call
                      ▼
┌──────────────────────────────────────────────────────────┐
│ CarBridge.sol (Solidity 0.8.23)                          │
│ 0x484cB35720a9bB6fcEA175e041A221408d01eC02               │
│ - Stores 103 cars                                        │
│ - Builds flat array [price, mileage, power, ...]         │
└─────────────────────┬────────────────────────────────────┘
                      │ staticcall
                      ▼
┌──────────────────────────────────────────────────────────┐
│ car_ranker (Stylus / Rust / WASM)                        │
│ 0xab304a3c48fd38492396d3719a8b2d16def14b8c               │
│ - 7 criteria ranking                                     │
│ - Size: 11.9 Kb                                          │
└──────────────────────────────────────────────────────────┘

### Ranking Criteria

The Stylus contract computes a weighted score based on **7 criteria**:

| # | Criterion | Direction | Normalization |
|---|---|---|---|
| 1 | 💰 Price per day | Lower is better | Inverted |
| 2 | 🛣️ Mileage | Lower is better | Inverted |
| 3 | ⚡ Engine power | Higher is better | Linear |
| 4 | ⭐ Rating | Higher is better | Linear |
| 5 | 📅 Year | Newer is better | Linear |
| 6 | ✨ Interior condition | Higher is better | Linear |
| 7 | 🔋 Fuel type | electric > hybrid > petrol > diesel | Pre-scored |

### Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Smart Contract (compute) | Rust + Stylus SDK 0.10.9 | On-chain ranking |
| Smart Contract (bridge) | Solidity 0.8.23 | Storage + delegation |
| Backend | Spring Boot 3.2.5, Java 21 | REST API |
| Blockchain client | Web3j 4.14.0 | Java ↔ Ethereum |
| Frontend | HTML + Vanilla JS | UI with sliders |
| Network | Robinhood Chain Testnet (Chain ID 46630) | Deployed |

### Quick Start

#### 1. Stylus contract (Rust)

```bash
cd stylus/car_ranker_v2
cargo stylus check --endpoint https://rpc.testnet.chain.robinhood.com
cargo stylus deploy \
  --endpoint https://rpc.testnet.chain.robinhood.com \
  --private-key-path .private_key

#### 2. Solidity bridge
cd contracts
forge build
forge create src/CarBridge.sol:CarBridge \
  --rpc-url https://rpc.testnet.chain.robinhood.com \
  --private-key $PRIVATE_KEY \
  --broadcast \
  --constructor-args <STYLUS_ADDRESS>

3. Spring Boot API
bash

cd java-api
export BLOCKCHAIN_PRIVATE_KEY=your_key_here
mvn clean package -DskipTests
java -jar target/java-api-0.0.1-SNAPSHOT.jar

4. Open frontend
text

http://localhost:8080/

REST API
Method	Endpoint	Description
GET	/api/cars/health	Blockchain connection check
GET	/api/cars/list	All cars (cached 5 min)
GET	/api/cars/{id}	One car by ID
POST	/api/cars/add	Add new car
POST	/api/cars/find-best	Find best by 7 criteria
Example: find-best
bash

curl -X POST http://localhost:8080/api/cars/find-best \
  -H "Content-Type: application/json" \
  -d '{
    "carIds": [0, 1, 2, 37, 41, 50],
    "weightPrice": 500,
    "weightMileage": 300,
    "weightPower": 200,
    "weightRating": 400,
    "weightYear": 100,
    "weightInterior": 50,
    "weightFuel": 800
  }'

Response:
json

{"bestCarId": 2, "bestCarScore": "49040840"}

Deployed Contracts

Network: Robinhood Chain Testnet (Chain ID 46630)
Contract	Address
Stylus car_ranker	0xab304a3c48fd38492396d3719a8b2d16def14b8c
Solidity CarBridge	0x484cB35720a9bB6fcEA175e041A221408d01eC02
Deployer	0xCc5640D6b3C13b7e21dfcb50db1752bF9c19F43b
Security

    Private key never committed to the repository

    Use environment variable BLOCKCHAIN_PRIVATE_KEY

    Use a separate wallet for testing

    .gitignore excludes .private_key, target/, out/

MIT — see LICENSE.
