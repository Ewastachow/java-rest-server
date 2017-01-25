package pl.edu.agh.kis.florist.dao;

import static pl.edu.agh.kis.florist.db.Tables.BOOKS;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import pl.edu.agh.kis.florist.db.tables.pojos.Books;
import pl.edu.agh.kis.florist.db.tables.records.BooksRecord;
import pl.edu.agh.kis.florist.model.Author;
import pl.edu.agh.kis.florist.model.Book;

public class BookDAO {
	
	private final String DB_URL = "jdbc:sqlite:test.db";
	private final AuthorDAO authorRepository = new AuthorDAO();
	
	public Book loadBookOfId(int bookId) {
		try (DSLContext create = DSL.using(DB_URL)) {
			BooksRecord record = create.selectFrom(BOOKS).where(BOOKS.ID.equal(bookId)).fetchOne();
			Book book = record.into(Book.class);
			List<Author> authors = authorRepository.loadAuthorsOfBookId(bookId);
			
			return book.withAuthors(authors);
		}
	}
	
	public List<Book> loadAllBooks() {
		try (DSLContext create = DSL.using(DB_URL)) {
			List<Books> books = create.selectFrom(BOOKS).fetchInto(Books.class);
			List<Book> newBooks = books.stream().map(Book::new)
					.map(  b -> b.withAuthors(
							authorRepository.loadAuthorsOfBookId(b.getId())
							)
						)
					.collect(Collectors.toList());
			return newBooks;
		}
	}
	
	public Book storeBook(Book book) {
		try (DSLContext create = DSL.using(DB_URL)) {
			
			BooksRecord record = create.newRecord(BOOKS,book);
			record.store();
			List<Author> stored = authorRepository.store(book.getAuthors());
			Book retrieved = new Book(record.into(Books.class));
			return retrieved.withAuthors(stored);
		}
	}
}
