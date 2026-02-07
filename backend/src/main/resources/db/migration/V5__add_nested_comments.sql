ALTER TABLE public.comments
    ADD COLUMN IF NOT EXISTS parent_comment_id BIGINT;

ALTER TABLE public.comments
    ADD CONSTRAINT fk_comments_parent
        FOREIGN KEY (parent_comment_id)
            REFERENCES public.comments (id);

CREATE INDEX IF NOT EXISTS idx_comments_parent_id ON public.comments (parent_comment_id);

ALTER TABLE public.comments
    ADD CONSTRAINT check_no_self_reference
        CHECK (id != parent_comment_id OR parent_comment_id IS NULL);

ALTER TABLE public.comments
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE public.comments
    ALTER COLUMN author_id DROP NOT NULL;