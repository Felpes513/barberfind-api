CREATE TABLE revoked_tokens (
    id         VARCHAR(36)  NOT NULL PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    revoked_at TIMESTAMP    NOT NULL
);