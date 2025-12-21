CREATE TABLE app_user (
    id VARCHAR(255) PRIMARY KEY,
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE request_log (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    topic VARCHAR(255),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE INDEX idx_request_log_user_id ON request_log(user_id);
