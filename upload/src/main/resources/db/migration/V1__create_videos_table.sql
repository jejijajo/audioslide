CREATE TABLE videos (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    privacy_status VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    category_id INTEGER,
    publish_at TIMESTAMP,
    made_for_kids BOOLEAN NOT NULL,
    thumbnail_s3_key VARCHAR(500),
    video_s3_key VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE video_tags (
    video_id UUID NOT NULL REFERENCES videos(id),
    tag VARCHAR(255)
);