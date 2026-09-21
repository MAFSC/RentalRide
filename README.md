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

1)Frontend (HTML + JS)                                     
http://localhost:8080/                                  
          │ REST API
          ▼

2) Spring Boot 3.2.5 (Java 21)                
CarController → CarRankingService → Web3j  

          │ eth_call
          ▼
3) CarBridge.sol (Solidity 0.8.23)                         
0x484cB35720a9bB6fcEA175e041A221408d01eC02              
- Stores 103 cars                                       
- Builds flat array [price, mileage, power, ...]        

          │ staticcall
          ▼
4) car_ranker (Stylus / Rust / WASM)                        
0xab304a3c48fd38492396d3719a8b2d16def14b8c               
- 7 criteria ranking                                    
- Size: 11.9 Kb                                         


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

Deployed Contracts

Network: Robinhood Chain Testnet (Chain ID 46630)
Contract	Address
Stylus car_ranker	0xab304a3c48fd38492396d3719a8b2d16def14b8c
Solidity CarBridge	0x484cB35720a9bB6fcEA175e041A221408d01eC02
Deployer	0xCc5640D6b3C13b7e21dfcb50db1752bF9c19F43b
Security

MIT — see LICENSE.
