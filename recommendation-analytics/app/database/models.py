from sqlalchemy import Column, BigInteger, Float, DateTime, String, Boolean, Index
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.sql import func
from datetime import datetime

Base = declarative_base()


class ViewedRecommendedPost(Base):
    __tablename__ = 'viewed_recommended_posts'

    user_id = Column(BigInteger, primary_key=True, nullable=False)
    post_id = Column(BigInteger, primary_key=True, nullable=False)
    author_id = Column(BigInteger, nullable=False)
    similarity_score = Column(Float, nullable=True)
    recency_score = Column(Float, nullable=True)
    engagement_score = Column(Float, default=0, nullable=False)
    weighted_sentiment_score = Column(Float, default=0, nullable=False)
    created_at = Column(DateTime, nullable=False)
    viewed_at = Column(DateTime, nullable=True)
    view_duration = Column(Float, nullable=True)
    reaction = Column(String, nullable=True)
    is_recommended = Column(Boolean, default=False, nullable=False)