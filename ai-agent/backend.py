import os
import json
import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
from openai import OpenAI
from dotenv import load_dotenv
from web3 import Web3

# ─── Setup ───
load_dotenv()

logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')
log = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app, origins="*")  # Разрешаем запросы с GitHub Pages

# DeepSeek (OpenAI-совместимый API)
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
if not DEEPSEEK_API_KEY:
    log.warning("DEEPSEEK_API_KEY not set. AI parsing will fail.")

client = OpenAI(
    api_key=DEEPSEEK_API_KEY,
    base_url="https://api.deepseek.com/v1"
) if DEEPSEEK_API_KEY else None

# Web3 (Robinhood Chain)
RPC_URL = os.getenv("RPC_URL", "https://rpc.testnet.chain.robinhood.com")
CAR_BRIDGE_ADDRESS = os.getenv("CAR_BRIDGE_ADDRESS", "0x484cB35720a9bB6fcEA175e041A221408d01eC02")

# ABI для чтения машин (view-функции)
CAR_BRIDGE_ABI = [
    {
        "inputs": [],
        "name": "carCount",
        "outputs": [{"internalType": "uint256", "name": "", "type": "uint256"}],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [{"internalType": "uint256", "name": "", "type": "uint256"}],
        "name": "cars",
        "outputs": [
            {"internalType": "string", "name": "brand", "type": "string"},
            {"internalType": "uint256", "name": "mileage", "type": "uint256"},
            {"internalType": "uint256", "name": "enginePower", "type": "uint256"},
            {"internalType": "string", "name": "fuelType", "type": "string"},
            {"internalType": "uint256", "name": "year", "type": "uint256"},
            {"internalType": "uint256", "name": "interiorScore", "type": "uint256"},
            {"internalType": "uint256", "name": "rating", "type": "uint256"},
            {"internalType": "uint256", "name": "pricePerDay", "type": "uint256"}
        ],
        "stateMutability": "view",
        "type": "function"
    }
]

w3 = Web3(Web3.HTTPProvider(RPC_URL))
contract = w3.eth.contract(address=Web3.to_checksum_address(CAR_BRIDGE_ADDRESS), abi=CAR_BRIDGE_ABI)

# ─── AI Prompt ───
SYSTEM_PROMPT = """
You are an AI assistant for a car rental service.
The user will ask for a car in natural language (Russian or English).
You must return a JSON object with weights for different car attributes and a rank.

Available attributes:
- price (стоимость аренды за день)
- mileage (пробег)
- power (мощность двигателя)
- rating (рейтинг)
- year (год выпуска)
- interior (оценка салона)
- fuel (тип топлива: electric, petrol, diesel, hybrid)

Weights must be integers from 1 to 1000.
- If the user wants a CHEAP car ("дешевый", "cheap", "budget"), set weight_price = 1000.
- If the user wants an EXPENSIVE/LUXURY car ("дорогой", "expensive", "luxury", "premium"), set weight_price = -1000 (negative).
- If the user wants a NEW car ("новый", "new"), set weight_year = 1000.
- If the user wants a POWERFUL car ("мощный", "powerful", "fast"), set weight_power = 1000.
- If the user wants a GOOD interior ("хороший салон", "best interior"), set weight_interior = 1000.
- If the user wants an ELECTRIC car ("электрический", "electric"), set weight_fuel = 1000.

If the user says "the most expensive", set weight_price = -1000 and rank = 1.
If the user says "the cheapest", set weight_price = 1000 and rank = 1.

Return ONLY a JSON object like this:
{
  "weights": {
    "weight_price": 1000,
    "weight_mileage": 10,
    "weight_power": 10,
    "weight_rating": 10,
    "weight_year": 10,
    "weight_interior": 10,
    "weight_fuel": 10
  },
  "rank": 1
}
"""

def parse_query(query):
    """Использует DeepSeek для парсинга запроса."""
    if not client:
        log.warning("No DeepSeek client, using default weights.")
        return {
            "weights": {
                "weight_price": 1000,
                "weight_mileage": 10,
                "weight_power": 10,
                "weight_rating": 10,
                "weight_year": 10,
                "weight_interior": 10,
                "weight_fuel": 10
            },
            "rank": 1
        }
    
    try:
        response = client.chat.completions.create(
            model="deepseek-chat",
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": query}
            ],
            temperature=0.1,
            max_tokens=200
        )
        content = response.choices[0].message.content.strip()
        # Извлекаем JSON (может быть обернут в ```json ... ```)
        if "```json" in content:
            content = content.split("```json")[1].split("```")[0].strip()
        elif "```" in content:
            content = content.split("```")[1].split("```")[0].strip()
        
        data = json.loads(content)
        log.info(f"Parsed weights: {data}")
        return data
    except Exception as e:
        log.error(f"DeepSeek parsing failed: {e}")
        # Fallback
        return {
            "weights": {
                "weight_price": 1000,
                "weight_mileage": 10,
                "weight_power": 10,
                "weight_rating": 10,
                "weight_year": 10,
                "weight_interior": 10,
                "weight_fuel": 10
            },
            "rank": 1
        }

def get_all_cars():
    """Получает все машины из контракта."""
    try:
        count = contract.functions.carCount().call()
        cars = []
        for i in range(count):
            car_data = contract.functions.cars(i).call()
            cars.append({
                "id": i,
                "brand": car_data[0],
                "mileage": car_data[1],
                "enginePower": car_data[2],
                "fuelType": car_data[3],
                "year": car_data[4],
                "interiorScore": car_data[5],
                "rating": car_data[6],
                "pricePerDay": car_data[7]
            })
        return cars
    except Exception as e:
        log.error(f"Error fetching cars: {e}")
        return []

def score_car(car, weights):
    """Вычисляет скор машины с учётом весов."""
    # Нормализация: приводим все значения к 0..100
    # Если weight_price отрицательный, инвертируем цену (чем дороже, тем лучше)
    price_weight = weights.get("weight_price", 10)
    if price_weight < 0:
        # Инвертируем: чем дороже, тем больше скор
        price_score = car["pricePerDay"] * abs(price_weight)
    else:
        # Чем дешевле, тем больше скор (максимум при 0)
        price_score = (1000 - car["pricePerDay"]) * price_weight

    mileage_score = (100000 - car["mileage"]) / 1000 * weights.get("weight_mileage", 10)
    power_score = car["enginePower"] * weights.get("weight_power", 10) / 10
    rating_score = car["rating"] * weights.get("weight_rating", 10) / 10
    year_score = (car["year"] - 2000) * weights.get("weight_year", 10) / 10
    interior_score = car["interiorScore"] * weights.get("weight_interior", 10) / 10
    fuel_score = weights.get("weight_fuel", 10) if car["fuelType"].lower() in ["electric", "hybrid"] else 0

    total = price_score + mileage_score + power_score + rating_score + year_score + interior_score + fuel_score
    return total

@app.route('/api/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "deepseek_configured": bool(DEEPSEEK_API_KEY),
        "redotpay_configured": False,
        "rpc": RPC_URL,
        "car_bridge": CAR_BRIDGE_ADDRESS
    })

@app.route('/api/parse', methods=['POST'])
def parse_endpoint():
    data = request.get_json()
    query = data.get("query", "").strip()
    if not query:
        return jsonify({"error": "query is required"}), 400

    # 1. Парсим запрос через DeepSeek
    parsed = parse_query(query)
    weights = parsed.get("weights", {})
    rank = parsed.get("rank", 1)

    # 2. Получаем машины
    cars = get_all_cars()
    if not cars:
        return jsonify({"error": "No cars found in contract"}), 500

    # 3. Сортируем машины на Python (чтобы поддерживать отрицательные веса)
    scored_cars = [(score_car(car, weights), car) for car in cars]
    scored_cars.sort(key=lambda x: x[0], reverse=True)

    # 4. Выбираем машину по рангу
    if rank < 1:
        rank = 1
    if rank > len(scored_cars):
        rank = len(scored_cars)
    
    best_car = scored_cars[rank - 1][1]

    # 5. Формируем ответ для фронтенда
    # Фронтенд ожидает weights и rank, а также bestCarId (хотя он сам его вычисляет)
    # Но теперь мы возвращаем bestCarId явно, чтобы фронт мог его использовать
    return jsonify({
        "weights": weights,
        "rank": rank,
        "bestCarId": best_car["id"],
        "bestCarScore": scored_cars[rank - 1][0]
    })

if __name__ == '__main__':
    log.info(f"Starting Flask on 0.0.0.0:5050")
    log.info(f"RPC: {RPC_URL}")
    log.info(f"CarBridge: {CAR_BRIDGE_ADDRESS}")
    app.run(host="0.0.0.0", port=5050, debug=False, threaded=True)

