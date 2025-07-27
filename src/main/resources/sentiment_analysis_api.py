from flask import Flask, request, jsonify, Response
from sentiment_analysis import analyze_sentiment
import json
import os
import base64
import bcrypt

app = Flask(__name__)

# Read credentials from environment variables
cli_username = os.getenv("API_USERNAME")
raw_password = os.getenv("API_PASSWORD")

if not cli_username or not raw_password:
    raise RuntimeError("API_USERNAME and API_PASSWORD environment variables must be set")

raw_users = {
    cli_username: raw_password,
}


auth_headers = {}
for username, password in raw_users.items():
    # Hash password (expensive, done only once)
    hashed_pw = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt())

    # Generate Base64 header for Basic Auth
    auth_string = f"{username}:{password}".encode("utf-8")
    encoded = base64.b64encode(auth_string).decode("utf-8")
    auth_headers[f"Basic {encoded}"] = username  # Cach


def check_auth(auth_header):
    """Fast check using precomputed headers (O(1) lookup)."""
    return auth_header in auth_headers


@app.before_request
def require_auth():
    auth_header = request.headers.get("Authorization")
    if not check_auth(auth_header):
        return Response("Unauthorized", status=401, headers={"WWW-Authenticate": 'Basic realm="Login Required"'})


@app.route('/analyze', methods=['GET', 'POST'])
def analyze():
    if request.method == 'GET':
        text = request.args.get('text', '')
    elif request.method == 'POST':
        text = request.get_data(as_text=True)

    sentiment_json = analyze_sentiment(text)
    response = json.loads(sentiment_json)
    return jsonify(response)
