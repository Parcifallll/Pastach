CREATE TABLE IF NOT EXISTS public.roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name IN ('USER', 'ADMIN'))
);

INSERT INTO public.roles (name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = 'USER');

INSERT INTO public.roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = 'ADMIN');

CREATE TABLE IF NOT EXISTS public.users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    birthday   DATE,
    locked     BOOLEAN               DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.posts
(
    id         BIGSERIAL PRIMARY KEY,
    author_id  BIGINT      NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    text       TEXT,
    photo_url  VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_posts_author_id ON public.posts(author_id);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON public.posts(created_at DESC);

CREATE TABLE IF NOT EXISTS public.user_roles
(
    user_id BIGINT NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES public.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON public.user_roles(role_id);