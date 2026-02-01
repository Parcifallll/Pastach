CREATE TABLE IF NOT EXISTS public.comments
(
    id         BIGSERIAL PRIMARY KEY,
    text       TEXT,
    photo_url  VARCHAR(255),
    post_id    BIGINT      NOT NULL REFERENCES public.posts (id) ON DELETE CASCADE,
    author_id  BIGINT      NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_comments_post_id ON public.comments(post_id);
CREATE INDEX IF NOT EXISTS idx_comments_author_id ON public.comments(author_id);
CREATE INDEX IF NOT EXISTS idx_comments_post_created ON public.comments(post_id, created_at DESC);