import os
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    APP_NAME: str = "RecommendationSystem"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False
    HOST: str = "0.0.0.0"
    PORT: int = 8000

    RECSYS_POSTGRES_HOST: str
    RECSYS_POSTGRES_PORT: int
    RECSYS_POSTGRES_USER: str
    RECSYS_POSTGRES_PASSWORD: str
    RECSYS_POSTGRES_DB: str

    RECSYS_GRPC_HOST: str
    RECSYS_GRPC_PORT: int

    @property
    def DATABASE_URL(self) -> str:
        return f"postgresql+asyncpg://{self.RECSYS_POSTGRES_USER}:{self.RECSYS_POSTGRES_PASSWORD}@{self.RECSYS_POSTGRES_HOST}:{self.RECSYS_POSTGRES_PORT}/{self.RECSYS_POSTGRES_DB}"

    @property
    def DATABASE_URL_SYNC(self) -> str:
        """Sync database URL for Alembic migrations (psycopg2 driver)"""
        return f"postgresql+psycopg2://{self.RECSYS_POSTGRES_USER}:{self.RECSYS_POSTGRES_PASSWORD}@{self.RECSYS_POSTGRES_HOST}:{self.RECSYS_POSTGRES_PORT}/{self.RECSYS_POSTGRES_DB}"

    RECSYS_REDIS_HOST: str
    RECSYS_REDIS_PORT: int
    RECSYS_REDIS_PASSWORD: str
    RECSYS_REDIS_DB: int
    RECSYS_REDIS_TTL: int

    KAFKA_BOOTSTRAP_SERVERS: str
    KAFKA_GROUP_ID: str = "rec-sys"
    KAFKA_AUTO_OFFSET_RESET: str = "earliest"

    MODEL_NAME: str = "sentence-transformers/all-MiniLM-L6-v2"
    EMBEDDING_DIMENSION: int = 384
    TOP_N_RECOMMENDATIONS: int = 20
    MIN_SIMILARITY_THRESHOLD: float = 0.1

    WEIGHT_CONTENT_SIMILARITY: float = 1.0
    WEIGHT_LIKE_BOOST: float = 1.0
    WEIGHT_DISLIKE_PENALTY: float = 0.5

    RECENCY_BOOST_1H: float = 2.0
    RECENCY_BOOST_6H: float = 1.8
    RECENCY_BOOST_24H: float = 1.5
    RECENCY_BOOST_3D: float = 1.3
    RECENCY_BOOST_7D: float = 1.1
    RECENCY_BOOST_DEFAULT: float = 1.0

    class Config:
        env_file = os.getenv("ENV_FILE", ".env.local")
        case_sensitive = True
        extra = "ignore"
settings = Settings()
