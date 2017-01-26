CREATE TABLE session_data (
    session_id TEXT PRIMARY KEY NOT NULL,
    user_id INTEGER REFERENCES users(id),
    last_accessed DATETIME 
);
