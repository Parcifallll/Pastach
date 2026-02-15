CREATE TABLE viewed_recommended_posts
(
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  BIGINT        NOT NULL,
    post_id                  BIGINT        NOT NULL,

    similarity_score         DECIMAL(6, 4) NOT NULL,      -- cosine similarity, get from rec-sys
    recency_score            DECIMAL(5, 3) NOT NULL,      -- recency boost, ge tfrom rec-sys
    final_score              DECIMAL(6, 4) NOT NULL,      -- similarity * recency

    weighted_sentiment_score DECIMAL(5, 3) DEFAULT 0.000, -- overall comments' sentiment

    viewed_at                TIMESTAMPTZ,
    view_duration            DECIMAL(6, 2) DEFAULT 0.00,
    reaction                 VARCHAR(20),

    CONSTRAINT fk_viewed_rec_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,

    CONSTRAINT fk_viewed_rec_post
        FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,

    UNIQUE (user_id, post_id)
);

CREATE UNIQUE INDEX idx_viewed_rec_user_post
    ON viewed_recommended_posts (user_id, post_id);

CREATE INDEX idx_viewed_rec_user_id
    ON viewed_recommended_posts (user_id);

CREATE INDEX idx_viewed_rec_post_id
    ON viewed_recommended_posts (post_id);

CREATE INDEX idx_viewed_rec_viewed_at
    ON viewed_recommended_posts (viewed_at);

CREATE INDEX idx_viewed_rec_final_score
    ON viewed_recommended_posts (final_score DESC);


ALTER TABLE comments
    ADD COLUMN IF NOT EXISTS sentiment_score DECIMAL(4, 3) DEFAULT 0.000;

CREATE INDEX idx_comments_sentiment_score
    ON comments (sentiment_score);