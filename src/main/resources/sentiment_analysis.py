from textblob import TextBlob
import json

def analyze_sentiment(text):

    analysis = TextBlob(text)
    sentiment_score = analysis.sentiment.polarity
    sentiment_classification = "positive" if sentiment_score > 0.1 else "negative" if sentiment_score < -0.1 else "neutral"

    return json.dumps({
        "score": sentiment_score,
        "classification": sentiment_classification
    })