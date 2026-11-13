-- V5__security.sql: Create app_user and user_role tables, seed user and admin with BCrypt hashes
CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(60)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,     -- BCrypt is 60 characters
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE user_role (
    user_id BIGINT      NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    role    VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- two users; the hashes are of user123 and admin123
INSERT INTO app_user (username, password_hash, enabled) VALUES
    ('user',  '$2a$10$vIVXiUD4LCExf0T5/nrMz.UK7X8jg9mDumA4.3jaiEmX3HC74j5bC', TRUE),
    ('admin', '$2a$10$I06NvZcv6KI0hNYaM761aOWCuLpuppKM8Sjxm/qmwvx.bpyMTXuaq', TRUE);

INSERT INTO user_role (user_id, role)
    SELECT id, 'USER'  FROM app_user;
INSERT INTO user_role (user_id, role)
    SELECT id, 'ADMIN' FROM app_user WHERE username = 'admin';
