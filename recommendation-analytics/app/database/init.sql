CREATE DATABASE IF NOT EXISTS rec_analytics_db;

CREATE TABLE IF NOT EXISTS rec_analytics_db.viewed_posts
(
    user_id                  UInt64,
    post_id                  UInt64,
    author_id                UInt64,
    similarity_score         Nullable(Float64),
    recency_score            Nullable(Float64),
    engagement_score         Float64 DEFAULT 0,
    weighted_sentiment_score Float64 DEFAULT 0,
    created_at               DateTime,
    viewed_at                Nullable(DateTime),
    view_duration            Nullable(Float64),
    reaction                 Nullable(String),
    is_recommended           Boolean DEFAULT false
)
    ENGINE = MergeTree()
PRIMARY KEY (user_id, post_id)
ORDER BY (user_id, post_id)
PARTITION BY toYYYYMM(created_at);