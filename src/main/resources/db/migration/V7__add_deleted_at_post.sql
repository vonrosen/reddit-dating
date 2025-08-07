ALTER TABLE post
    ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;

CREATE INDEX idx_deleted_at ON post (deleted_at);