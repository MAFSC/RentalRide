#!/usr/bin/env python3
"""
RentalRide AI Agent — Flask Backend
====================================

Handles:
  1. Natural-language parsing via DeepSeek LLM → 7 weights + rank
  2. RedotPay Connect preOrder creation (demo placeholder)

The frontend is hosted on GitHub Pages:
  https://mafsc.github.io/RentalRide/

This backend runs on the VPS and is called via CORS:
  http://194.5.152.242:5050/api/*

Usage:
    python backend.py
    # → Flask listening on 0.0.0.0:5050
"""

import json
import logging
import os
import time

from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS
from openai import OpenAI

# ─────────────────────────────────────────────────────────────
#  Logging
# ─────────────────────────────────────────────────────────────

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)
log = logging.getLogger(__name__)

# ─────────────────────────────────────────────────────────────
#  Environment
# ─────────────────────────────────────────────────────────────

load_dotenv()

DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
REDOTPAY_PREORDER_ID = os.getenv("REDOTPAY_PREORDER_ID", "")

if not DEEPSEEK_API_KEY:
    raise RuntimeError(
        "DEEPSEEK_API_KEY is not set. Add it to /1/RentalRide/ai-agent/.env"
    )

# ─────────────────────────────────────────────────────────────
#  Flask app with CORS for GitHub Pages
# ─────────────────────────────────────────────────────────────

app = Flask(__name__)

CORS(
    app,
    resources={
        r"/api/*": {
            "origins": [
                "https://mafsc.github.io",      # GitHub Pages
                "http://194.5.152.242:8080",    # Local frontend (fallback)
                "http://localhost:8080",
                "http://127.0.0.1:8080",
            ],
            "methods": ["GET", "POST", "OPTIONS"],
            "allow_headers": ["Content-Type"],
            "supports_credentials": False,
            "max_age": 3600,
        }
    },
)


# Allow Private Network Access (Chrome) for mixed HTTP/HTTPS scenarios
@app.after_request
def add_pna_header(response):
    if request.headers.get("Access-Control-Request-Private-Network") == "true":
        response.headers["Access-Control-Allow-Private-Network"] = "true"
    return response


# ─────────────────────────────────────────────────────────────
#  DeepSeek client
# ─────────────────────────────────────────────────────────────

deepseek_client = OpenAI(
    api_key=DEEPSEEK_API_KEY,
    base_url="https://api.deepseek.com",
    timeout=55.0,
)

DEEPSEEK_MODEL = "deepseek-flash"

# ─────────────────────────────────────────────────────────────
#  System prompt
# ─────────────────────────────────────────────────────────────

SYSTEM_PROMPT = """
You are a request parser for a car rental smart contract.

Return a JSON object with TWO parts:

1. "weights" (0..1000):
   - weight_price:    higher weight → cheaper cars preferred
   - weight_mileage:  higher weight → lower mileage preferred
   - weight_power:    higher weight → more horsepower preferred
   - weight_rating:   higher weight → higher user rating preferred
   - weight_year:     higher weight → newer cars preferred
   - weight_interior: higher weight → better interior condition preferred
   - weight_fuel:     higher weight → electric / hybrid preferred
                      (fuel priority: electric > hybrid > petrol > diesel)

2. "rank" (1-based):
   - 1 = best car (default)
   - 2 = second best car
   - 3 = third best car

Rules:
  - Explicit mention of a criterion → weight 1000
  - Implied mention → weight 500
  - Not mentioned → weight 10
  - "second"/"второй"/"2nd" → rank 2
  - "third"/"третий"/"3rd" → rank 3
  - No rank mentioned → rank 1

Output (strict JSON, no other text):
{
  "weights": {
    "weight_price": <int>,
    "weight_mileage": <int>,
    "weight_power": <int>,
    "weight_rating": <int>,
    "weight_year": <int>,
    "weight_interior": <int>,
    "weight_fuel": <int>
  },
  "rank": <int>
}
"""

# ─────────────────────────────────────────────────────────────
#  Route: /api/parse
# ─────────────────────────────────────────────────────────────

@app.route("/api/parse", methods=["POST", "OPTIONS"])
def parse_request():
    if request.method == "OPTIONS":
        return "", 204

    start = time.time()
    data = request.get_json(silent=True) or {}
    user_request = data.get("query", "").strip()

    if not user_request:
        return jsonify({"error": "empty query"}), 400

    log.info(f"Parsing query: {user_request!r}")

    try:
        response = deepseek_client.chat.completions.create(
            model=DEEPSEEK_MODEL,
            temperature=0,
            response_format={"type": "json_object"},
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT.strip()},
                {"role": "user", "content": user_request},
            ],
        )

        raw = response.choices[0].message.content
        parsed = json.loads(raw)

        weights = parsed.get("weights", parsed)
        rank = int(parsed.get("rank", 1))

        keys = [
            "weight_price", "weight_mileage", "weight_power",
            "weight_rating", "weight_year", "weight_interior", "weight_fuel",
        ]
        for k in keys:
            weights[k] = max(0, min(int(weights.get(k, 10)), 1000))

        elapsed = time.time() - start
        log.info(f"Parsed in {elapsed:.2f}s → rank={rank}, weights={weights}")

        return jsonify({"weights": weights, "rank": max(1, rank)})

    except Exception as e:
        elapsed = time.time() - start
        log.exception(f"DeepSeek parse failed after {elapsed:.2f}s")
        return jsonify({"error": f"LLM error: {str(e)}"}), 500


# ─────────────────────────────────────────────────────────────
#  Route: /api/rent
# ─────────────────────────────────────────────────────────────

@app.route("/api/rent", methods=["POST", "OPTIONS"])
def rent_car():
    if request.method == "OPTIONS":
        return "", 204

    data = request.get_json(silent=True) or {}
    car_id = data.get("carId")
    brand = data.get("brand", "Unknown")
    price = data.get("pricePerDay", 0)

    if not car_id:
        return jsonify({"error": "missing carId"}), 400

    if not REDOTPAY_PREORDER_ID:
        return jsonify({
            "error": (
                "RedotPay Connect is not configured. "
                "The landlord must register as a merchant on RedotPay, "
                "create a preOrder, and set REDOTPAY_PREORDER_ID in .env"
            )
        }), 503

    log.info(f"Rent request: carId={car_id}, brand={brand}, price={price}")

    return jsonify({
        "preOrderId": REDOTPAY_PREORDER_ID,
        "carId": car_id,
        "brand": brand,
        "price": price,
    })


# ─────────────────────────────────────────────────────────────
#  Route: /api/health
# ─────────────────────────────────────────────────────────────

@app.route("/api/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "deepseek_configured": bool(DEEPSEEK_API_KEY),
        "redotpay_configured": bool(REDOTPAY_PREORDER_ID),
    })


# ─────────────────────────────────────────────────────────────
#  Entry point
# ─────────────────────────────────────────────────────────────

if __name__ == "__main__":
    log.info("Starting Flask on 0.0.0.0:5050")
    app.run(host="0.0.0.0", port=5050, debug=False, threaded=True)
