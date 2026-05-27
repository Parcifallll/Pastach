import json
import logging

from kafka.consumer.group import KafkaConsumer
from kafka.errors import KafkaError

from app.config import settings
from app.database.clickhouse_client import ClickHouseClient
from app.services.comment_sentiment_service import SentimentService
from .event_payloads import (
    RecommendationCreatedPayload,
    RecommendationViewedPayload,
    RecommendationReactedPayload,
    RecommendationSentimentPayload,
    RecommendationEvent
)

logging.basicConfig(level=settings.LOG_LEVEL)
logger = logging.getLogger(__name__)


class RecommendationAnalyticsListener:

    def __init__(self):
        self.clickhouse_client = ClickHouseClient()
        self.sentiment_service = SentimentService()
        self.consumer = None

    def start(self):
        logger.info(f"Starting Kafka consumer: {settings.KAFKA_BOOTSTRAP_SERVERS}")

        self.consumer = KafkaConsumer(
            settings.KAFKA_TOPIC,
            bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS.split(','),
            group_id=settings.KAFKA_GROUP_ID,
            auto_offset_reset=settings.KAFKA_AUTO_OFFSET_RESET,
            enable_auto_commit=True,
            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
            session_timeout_ms=30000,
            request_timeout_ms=60000,
        )

        logger.info("Connected to Kafka")

        try:
            for message in self.consumer:
                self._process_event(message.value)
        except KafkaError as e:
            logger.error(f"Kafka error: {e}")
        except Exception as e:
            logger.error(f"Error: {e}", exc_info=True)

    def _process_event(self, event: dict):
        try:
            event_obj = RecommendationEvent(**event)
            event_type = event_obj.eventType

            if event_type == 'recommendation.received':
                payload = RecommendationCreatedPayload(**event_obj.payload)
                self._handle_recommendation_received(payload)
            elif event_type == 'recommendation.viewed':
                payload = RecommendationViewedPayload(**event_obj.payload)
                self._handle_recommendation_viewed(payload)
            elif event_type == 'recommendation.reacted':
                payload = RecommendationReactedPayload(**event_obj.payload)
                self._handle_recommendation_reacted(payload)
            elif event_type == 'recommendation.sentiment.updated':
                payload = RecommendationSentimentPayload(**event_obj.payload)
                self._handle_recommendation_sentiment(payload)
            else:
                logger.warning(f"Unknown event type: {event_type}")

        except Exception as e:
            logger.error(f"Error processing event: {e}", exc_info=True)

    def _handle_recommendation_received(self, payload: RecommendationCreatedPayload):
        logger.info(f"Received: user={payload.userId}, post={payload.postId}")

        self.clickhouse_client.insert_recommendation(
            user_id=payload.userId,
            post_id=payload.postId,
            author_id=payload.authorId,
            similarity_score=payload.similarityScore,
            recency_score=payload.recencyScore,
            created_at=payload.createdAt,
            is_recommended=True
        )

    def _handle_recommendation_viewed(self, payload: RecommendationViewedPayload):
        logger.info(f"Viewed: user={payload.userId}, post={payload.postId}")

        self.clickhouse_client.update_recommendation_view(
            user_id=payload.userId,
            post_id=payload.postId,
            viewed_at=payload.viewedAt,
            view_duration=payload.viewDuration
        )

    def _handle_recommendation_reacted(self, payload: RecommendationReactedPayload):
        logger.info(f"Reacted: user={payload.userId}, post={payload.postId}")

        self.clickhouse_client.update_recommendation_reaction(
            user_id=payload.userId,
            post_id=payload.postId,
            reaction=payload.reaction
        )

    def _handle_recommendation_sentiment(self, payload: RecommendationSentimentPayload):
        logger.info(f"Sentiment update: user={payload.userId}, post={payload.postId}")
        s_new = self.sentiment_service.compute_score(payload.commentText)
        old_score = self.clickhouse_client.get_sentiment_score(
            user_id=payload.userId,
            post_id=payload.postId
        )

        # EMA: new_score = alpha * s_new + (1 - alpha) * old_score
        new_score = settings.EMA_ALPHA * s_new + (1 - settings.EMA_ALPHA) * old_score

        self.clickhouse_client.update_recommendation_sentiment(
            user_id=payload.userId,
            post_id=payload.postId,
            weighted_sentiment_score=new_score
        )