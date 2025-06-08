from textblob import TextBlob

def analyze_sentiment(text):

    analysis = TextBlob(text)
    sentiment_score = analysis.sentiment.polarity
    sentiment_classification = "positive" if sentiment_score > 0 else "negative" if sentiment_score < 0 else "neutral"

    return {
        "score": sentiment_score,
        "classification": sentiment_classification
    }
    
if __name__ == '__main__':
    # Example usage and testing
    test_cases = [
        "This is a great movie!",
        "I hate this product.",
        "The weather is just okay.",
        "A long and boring lecture"
    ]

    for test_case in test_cases:
        result = analyze_sentiment(test_case)
        print(f"Text: {test_case}")
        print(f"Sentiment Score: {result['score']}")
        print(f"Classification: {result['classification']}")
        print("-" * 30)