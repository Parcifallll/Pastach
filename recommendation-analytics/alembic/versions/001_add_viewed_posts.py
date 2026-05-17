from alembic import op

revision = '001'
down_revision = None
branch_labels = None
depends_on = None

def upgrade() -> None:
    op.execute("""
               CREATE TABLE IF NOT EXISTS viewed_posts
               (
                   user_id                  UInt64,
                   post_id                  UInt64,
                   author_id                UInt64,

                   similarity_score         Nullable(Float64),
                   recency_score            Nullable(Float64),
                   engagement_score         Float64 DEFAULT 0,
                   weighted_sentiment_score Float64  DEFAULT 0,
                   created_at               DateTime,
                   viewed_at                Nullable(DateTime),
                   view_duration            Nullable(Float64),
                   reaction                 Nullable(String),
                   is_recommended           Boolean  DEFAULT false
               ) ENGINE = MergeTree()
                PRIMARY KEY (user_id, post_id)
                ORDER BY (user_id, post_id)
                PARTITION BY toYYYYMM(viewed_at)
                INDEX idx_is_recommended (is_recommended) TYPE set GRANULARITY 1
               """)

def downgrade() -> None:
    op.execute("DROP TABLE IF EXISTS viewed_posts")