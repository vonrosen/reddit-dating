CREATE TABLE IF NOT EXISTS `matches` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1 VARCHAR(255) NOT NULL,
    user1_post_url VARCHAR(255) NOT NULL,
    user2 VARCHAR(255) NOT NULL,
    user2_post_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_unique_user1_user2 ON `matches`(user1, user2);
