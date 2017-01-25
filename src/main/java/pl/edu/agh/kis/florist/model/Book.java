package pl.edu.agh.kis.florist.model;

import java.util.List;

import pl.edu.agh.kis.florist.db.tables.pojos.Books;

public class Book extends Books {
	
	public Book(Books b) {
		super(b);
	}

	public Book(Integer id, String name, String isbn) {
		super(id, name, isbn);
	}

	public Book(String name, String isbn) {
		this(null, name, isbn);
	}

	public Book(Integer id, String name, String isbn, List<Author> authors) {
		this(id, name, isbn);
		this.authors = authors;
	}

	public Book(String name, String isbn, List<Author> authors) {
		this(null, name, isbn, authors);
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public Book withAuthors(List<Author> authors) {
		if (this.authors != null && !this.authors.isEmpty()) {
			throw new IllegalStateException("this book already has authors!");
		}
		return new Book(getId(), getName(), getIsbn(), authors);
	}

	private List<Author> authors;
	private static final long serialVersionUID = -5684919906468290041L;
}
