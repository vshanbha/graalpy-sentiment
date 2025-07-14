from flask import Flask, request, jsonify
from sentiment_analysis import analyze_sentiment
import json

app = Flask(__name__)

@app.route('/analyze', methods=['GET', 'POST'])
def analyze():
    if request.method == 'GET':
        text = request.args.get('text', '')
    elif request.method == 'POST':
        text = request.get_data(as_text=True)

    sentiment_json = analyze_sentiment(text)
    response = json.loads(sentiment_json)

    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=False)
