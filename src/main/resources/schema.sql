CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) NOT NULL UNIQUE,
    username VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    birthday DATE
    );

CREATE UNIQUE INDEX IF NOT EXISTS users_id_unindex ON users (id);


CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    author_id VARCHAR(64) NOT NULL,
    post_text VARCHAR(1000),
    photo_url VARCHAR(1000),
    creation_date DATE DEFAULT CURRENT_DATE,
    CONSTRAINT fk_post_author
    FOREIGN KEY(author_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

CREATE UNIQUE INDEX IF NOT EXISTS post_id_unindex ON posts (id);