import logging
from datetime import datetime
import clickhouse_connect

from app.config import settings

logger = logging.getLogger(__name__)


class ClickHouseClient:

    def __init__(self):
        self.client = clickhouse_connect.get_client(
            host=settings.REC_ANALYTICS_CLICKHOUSE_HOST,
            port=settings.REC_ANALYTICS_CLICKHOUSE_PORT,
            username=settings.REC_ANALYTICS_CLICKHOUSE_USER,
            password=settings.REC_ANALYTICS_CLICKHOUSE_PASSWORD,
            database=settings.REC_ANALYTICS_CLICKHOUSE_DB
        )

    def insert_recommendation(
            self,
            user_id: int,
            post_id: int,
            author_id: int,
            similarity_score: float,
            recency_score: float,
            created_at: str,
            is_recommended: bool = True
    ):
        try:
            created_at_dt = datetime.fromisoformat(created_at.replace('Z', '+00:00'))

            query = """
                    INSERT INTO viewed_recommended_posts (
                        user_id, post_id, author_id, similarity_score, recency_score,
                        engagement_score, weighted_sentiment_score, created_at,
                        viewed_at, view_duration, reaction, is_recommended
                    ) VALUES \
                    """

            data = [(
                user_id,
                post_id,
                author_id,
                similarity_score,
                recency_score,
                0.0,  # engagement_score
                0.0,  # weighted_sentiment_score
                created_at_dt,
                None,  # viewed_at
                None,  # view_duration
                None,  # reaction
                is_recommended
            )]

            self.client.insert(
                table='viewed_recommended_posts',
                data=data,
                column_names=[
                    'user_id', 'post_id', 'author_id', 'similarity_score',
                    'recency_score', 'engagement_score', 'weighted_sentiment_score',
                    'created_at', 'viewed_at', 'view_duration', 'reaction', 'is_recommended'
                ]
            )

            logger.info(f"Inserted recommendation: user={user_id}, post={post_id}")

        except Exception as e:
            logger.error(f"Error inserting: {e}", exc_info=True)
            raise

    def update_recommendation_view(
            self,
            user_id: int,
            post_id: int,
            viewed_at: str,
            view_duration: float
    ):
        try:
            viewed_at_dt = datetime.fromisoformat(viewed_at.replace('Z', '+00:00'))

            query = f"""
                ALTER TABLE viewed_recommended_posts
                UPDATE 
                    viewed_at = '{viewed_at_dt.strftime('%Y-%m-%d %H:%M:%S')}',
                    view_duration = {view_duration}
                WHERE user_id = {user_id} AND post_id = {post_id}
            """

            self.client.command(query)

            logger.info(f"Updated view: user={user_id}, post={post_id}")

        except Exception as e:
            logger.error(f"Error updating view: {e}", exc_info=True)
            raise

    def update_recommendation_reaction(
            self,
            user_id: int,
            post_id: int,
            reaction: str
    ):
        try:
            query = f"""
                ALTER TABLE viewed_recommended_posts
                UPDATE reaction = '{reaction}'
                WHERE user_id = {user_id} AND post_id = {post_id}
            """

            self.client.command(query)

            logger.info(f"Updated reaction: user={user_id}, post={post_id}")

        except Exception as e:
            logger.error(f"Error updating reaction: {e}", exc_info=True)
            raise

    def update_recommendation_sentiment(
            self,
            user_id: int,
            post_id: int,
            weighted_sentiment_score: float
    ):
        try:
            query = f"""
                ALTER TABLE viewed_recommended_posts
                UPDATE weighted_sentiment_score = {weighted_sentiment_score}
                WHERE user_id = {user_id} AND post_id = {post_id}
            """

            self.client.command(query)

            logger.info(f"Updated sentiment: user={user_id}, post={post_id}")

        except Exception as e:
            logger.error(f"Error updating sentiment: {e}", exc_info=True)
            raise