ALTER TABLE comments
    ADD COLUMN IF NOT EXISTS sentiment_score DECIMAL(4, 3) DEFAULT 0.000;

CREATE INDEX idx_comments_sentiment_score
    ON comments (sentiment_score);