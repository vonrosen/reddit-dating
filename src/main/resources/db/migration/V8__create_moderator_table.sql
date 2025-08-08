CREATE TABLE IF NOT EXISTS `moderator` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    auth_token TEXT,
    auth_token_type VARCHAR(255),
    auth_token_scope VARCHAR(255),
    auth_request_state_token VARCHAR(255),
    auth_token_expires_at TIMESTAMP,
    refresh_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);