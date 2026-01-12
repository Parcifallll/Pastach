CREATE TABLE IF NOT EXISTS public.reactions
(
    id          BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL CHECK (target_type IN ('POST', 'COMMENT')),
    target_id   BIGINT      NOT NULL,
    author_id   BIGINT      NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    type        VARCHAR(20) NOT NULL CHECK (type IN ('LIKE', 'DISLIKE')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT reactions_unique_per_target UNIQUE (target_type, target_id, author_id)
);

CREATE INDEX IF NOT EXISTS idx_reactions_target ON public.reactions(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_reactions_author ON public.reactions(author_id);