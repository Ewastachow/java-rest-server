CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_name VARCHAR(100) UNIQUE,
    display_name VARCHAR(100),
    hashed_password VARCHAR(100) NOT NULL
);
