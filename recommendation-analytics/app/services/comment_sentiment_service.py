import logging
from transformers import pipeline

logger = logging.getLogger(__name__)

LABEL_WEIGHTS = {
    "Very Negative": -1.0,
    "Negative":      -0.5,
    "Neutral":        0.0,
    "Positive":       0.5,
    "Very Positive":  1.0,
}


class SentimentService:

    def __init__(self):
        logger.info("Loading tabularisai/multilingual-sentiment-analysis model...")
        self.classifier = pipeline(
            "text-classification",
            model="tabularisai/multilingual-sentiment-analysis",
            top_k=None
        )
        logger.info("Sentiment model loaded successfully")

    def compute_score(self, text: str) -> float:
        results = self.classifier(text)
        score = sum(
            LABEL_WEIGHTS.get(item["label"], 0.0) * item["score"]
            for item in results
        )
        return round(float(score), 6)