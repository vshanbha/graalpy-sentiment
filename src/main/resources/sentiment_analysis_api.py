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

# Hash password at startup
hashed_password = bcrypt.hashpw(raw_password.encode('utf-8'), bcrypt.gensalt())


def check_auth(auth_header):
    """Validate Basic Auth header."""
    if not auth_header or not auth_header.startswith("Basic "):
        return False

    try:
        encoded_credentials = auth_header.split(" ")[1]
        decoded_credentials = base64.b64decode(encoded_credentials).decode("utf-8")
        username, password = decoded_credentials.split(":", 1)
    except Exception:
        return False

    # Check username and bcrypt password
    return username == cli_username and bcrypt.checkpw(password.encode('utf-8'), hashed_password)


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
