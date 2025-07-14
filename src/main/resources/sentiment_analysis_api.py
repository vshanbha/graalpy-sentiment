from flask import Flask, request, jsonify
from textblob import TextBlob
import json

app = Flask(__name__)

@app.route('/analyze', methods=['GET', 'POST'])
def analyze():
    if request.method == 'GET':
        text = request.args.get('text', '')
    elif request.method == 'POST':
        text = request.get_data(as_text=True)

    analysis = TextBlob(text)
    sentiment_score = analysis.sentiment.polarity
    sentiment_classification = (
        "positive" if sentiment_score > 0.1 else
        "negative" if sentiment_score < -0.1 else
        "neutral"
    )

    response = {
        "score": sentiment_score,
        "classification": sentiment_classification
    }

    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=False)
