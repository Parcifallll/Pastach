INSERT INTO users (id, username, email, birthday)
SELECT 'hehe_boy', 'Roman', '892v34@gmail.com', '2005-12-23'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'hehe_boy');

INSERT INTO posts (author_id, text, photo_url)
SELECT 'hehe_boy', 'text', 'url'
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE author_id = 'hehe_boy' AND text = 'text');

INSERT INTO posts (author_id, text, photo_url)
SELECT 'hehe_boy', 'text', 'url'
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE author_id = 'hehe_boy' AND text = 'texdfst');