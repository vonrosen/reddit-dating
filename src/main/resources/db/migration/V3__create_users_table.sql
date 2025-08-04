CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    auth_token TEXT,
    auth_token_type VARCHAR(255),
    auth_token_scope VARCHAR(255),
    auth_request_state_token VARCHAR(255),
    auth_token_expires_at TIMESTAMP,
    refresh_token VARCHAR(255),
    registration_message_sent_at TIMESTAMP,
    registered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_unique_user_name ON `user`(user_name);
CREATE UNIQUE INDEX idx_auth_request_state_token ON `user`(auth_request_state_token);
CREATE INDEX idx_registration_message_sent_at ON `user`(registration_message_sent_at);
CREATE INDEX idx_registered_at ON `user`(registered_at);