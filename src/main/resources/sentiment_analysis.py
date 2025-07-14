import json
import time
from textblob import TextBlob

def analyze_sentiment(text):
    start = time.time()
    
    analysis = TextBlob(text)
    sentiment_score = analysis.sentiment.polarity
    sentiment_classification = (
        "positive" if sentiment_score > 0.1 else
        "negative" if sentiment_score < -0.1 else
        "neutral"
    )
    
    end = time.time()
    print(f"[Python] Took {(end - start) * 1000:.2f} ms")

    return json.dumps({
        "score": sentiment_score,
        "classification": sentiment_classification
    })