ALTER TABLE post
    ADD COLUMN is_nsfw BOOLEAN;

CREATE INDEX idx_post_is_nsfw ON post (is_nsfw);