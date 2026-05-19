CREATE TABLE IF NOT EXISTS processed_resume_events (
    event_id     VARCHAR      PRIMARY KEY,
    processed_at TIMESTAMPTZ  NOT NULL
);
