CREATE TABLE author_books (
	author_id INTEGER,
	book_id INTEGER,
	PRIMARY KEY (author_id,book_id),
	FOREIGN KEY (author_id) REFERENCES authors(id),
	FOREIGN KEY (book_id) REFERENCES books(id)
);
