CREATE TABLE file_metadata (
    file_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    path_lower TEXT NOT NULL,
    path_display TEXT NOT NULL,
    parent_folder_id INTEGER REFERENCES folder_metadata(folder_id),
    size INTEGER NOT NULL,
    server_created_at DATETIME,
    server_changed_at DATETIME
);
