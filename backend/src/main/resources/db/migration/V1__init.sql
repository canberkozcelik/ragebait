CREATE TABLE ragebait_post (
    id SERIAL PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    content VARCHAR(280) NOT NULL,
    created_at TIMESTAMP NOT NULL
);