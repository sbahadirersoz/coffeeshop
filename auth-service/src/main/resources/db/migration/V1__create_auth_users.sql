CREATE TABLE auth_users (
    id            UUID         PRIMARY KEY,
    username      VARCHAR(12)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    version       BIGINT       NOT NULL
);

CREATE TABLE auth_user_roles (
    user_id   UUID        NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    role_type VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role_type)
);
