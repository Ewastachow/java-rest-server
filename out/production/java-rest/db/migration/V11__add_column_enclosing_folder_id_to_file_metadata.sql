ALTER TABLE file_metadata ADD COLUMN enclosing_folder_id INTEGER REFERENCES folder_metadata(folder_id);
