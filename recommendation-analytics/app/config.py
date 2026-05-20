import os
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    APP_NAME: str = "AnalyticsService"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False
    LOG_LEVEL: str = "INFO"

    REC_ANALYTICS_CLICKHOUSE_HOST: str
    REC_ANALYTICS_CLICKHOUSE_PORT: int
    REC_ANALYTICS_CLICKHOUSE_USER: str
    REC_ANALYTICS_CLICKHOUSE_PASSWORD: str
    REC_ANALYTICS_CLICKHOUSE_DB: str
    REC_ANALYTICS_CLICKHOUSE_URL: str

    KAFKA_BOOTSTRAP_SERVERS: str
    KAFKA_GROUP_ID: str = "analytics-service"
    KAFKA_AUTO_OFFSET_RESET: str = "earliest"
    KAFKA_TOPIC: str = "pastach.recommendations"

    class Config:
        env_file = os.getenv("ENV_FILE", ".env.local")
        case_sensitive = True
        extra = "ignore"


settings = Settings()
