import logging
from datetime import datetime
from sqlalchemy import create_engine, update
from sqlalchemy.orm import Session

from app.config import settings
from .models import ViewedRecommendedPost

logger = logging.getLogger(__name__)


class ClickHouseClient:

    def __init__(self):
        connection_string = f"clickhouse+native://{settings.REC_ANALYTICS_CLICKHOUSE_USER}:{settings.REC_ANALYTICS_CLICKHOUSE_PASSWORD}@{settings.REC_ANALYTICS_CLICKHOUSE_HOST}:{settings.REC_ANALYTICS_CLICKHOUSE_PORT}/{settings.REC_ANALYTICS_CLICKHOUSE_DB}"

        self.engine = create_engine(
            connection_string,
            echo=settings.DEBUG
        )

    def get_session(self) -> Session:
        return Session(self.engine)

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

            session = self.get_session()

            record = ViewedRecommendedPost(
                user_id=user_id,
                post_id=post_id,
                author_id=author_id,
                similarity_score=similarity_score,
                recency_score=recency_score,
                created_at=created_at_dt,
                is_recommended=is_recommended
            )

            session.add(record)
            session.commit()
            session.close()

            logger.info(f"Inserted: user={user_id}, post={post_id}")

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

            session = self.get_session()

            stmt = update(ViewedRecommendedPost).where(
                (ViewedRecommendedPost.user_id == user_id) &
                (ViewedRecommendedPost.post_id == post_id)
            ).values(
                viewed_at=viewed_at_dt,
                view_duration=view_duration
            )

            session.execute(stmt)
            session.commit()
            session.close()

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
            session = self.get_session()

            stmt = update(ViewedRecommendedPost).where(
                (ViewedRecommendedPost.user_id == user_id) &
                (ViewedRecommendedPost.post_id == post_id)
            ).values(
                reaction=reaction
            )

            session.execute(stmt)
            session.commit()
            session.close()

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
            session = self.get_session()

            stmt = update(ViewedRecommendedPost).where(
                (ViewedRecommendedPost.user_id == user_id) &
                (ViewedRecommendedPost.post_id == post_id)
            ).values(
                weighted_sentiment_score=weighted_sentiment_score
            )

            session.execute(stmt)
            session.commit()
            session.close()

            logger.info(f"Updated sentiment: post={post_id}")

        except Exception as e:
            logger.error(f"Error updating sentiment: {e}", exc_info=True)
            raise