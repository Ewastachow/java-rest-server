package pl.edu.agh.kis.florist.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static pl.edu.agh.kis.florist.db.Tables.AUTHORS;
import static pl.edu.agh.kis.florist.db.Tables.AUTHOR_BOOKS;
import static pl.edu.agh.kis.florist.db.Tables.BOOKS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.kis.florist.db.tables.pojos.AuthorBooks;
import pl.edu.agh.kis.florist.db.tables.pojos.Books;
import pl.edu.agh.kis.florist.db.tables.records.AuthorBooksRecord;
import pl.edu.agh.kis.florist.db.tables.records.AuthorsRecord;
import pl.edu.agh.kis.florist.db.tables.records.BooksRecord;
import pl.edu.agh.kis.florist.model.Author;
import pl.edu.agh.kis.florist.model.Book;

public class BookDAOTest {

	private final String DB_URL = "jdbc:sqlite:test.db";
	private DSLContext create;

	@Before
	public void setUp() {
		create = DSL.using(DB_URL);
		// clean up all tables
		create.deleteFrom(AUTHOR_BOOKS).execute();
		create.deleteFrom(AUTHORS).execute();
		create.deleteFrom(BOOKS).execute();
	}

	@After
	public void tearDown() {
		create.close();
	}

	@Test
	public void storeSingleBookWithOneAuthor() {

		// setup:
		List<Author> as = Arrays.asList(new Author("Michał", "Bułchakow"));
		Book b = new Book("Mistrz i Małgorzata", "89898989", as);

		// when:
		Book book = new BookDAO().storeBook(b);

		// then:
		assertNotNull(book);
		assertThat(book).extracting(Book::getName, Book::getIsbn).containsOnly("Mistrz i Małgorzata", "89898989");
		assertThat(book.getId()).isGreaterThan(0);
		List<Author> extractedAuthors = book.getAuthors();
		assertThat(extractedAuthors).hasSize(1);
		assertThat(extractedAuthors.get(0)).extracting(Author::getFirstName, Author::getLastName).containsOnly("Michał",
				"Bułchakow");
		assertThat(extractedAuthors.get(0).getId()).isGreaterThan(0);
	}

	@Test
	public void loadAllBooksFetchesSingleInsertedBookWithOneAuthor() {

		// setup:
		List<Author> as = Arrays.asList(new Author("Michał", "Bułchakow"));
		AuthorsRecord aRec = create.newRecord(AUTHORS, as.get(0));
		aRec.store();

		Book b = new Book("Mistrz i Małgorzata", "89898989", as);
		BooksRecord bRec = create.newRecord(BOOKS, b);
		bRec.store();

		AuthorBooksRecord jRec = create.newRecord(AUTHOR_BOOKS, new AuthorBooks(aRec.getId(), bRec.getId()));
		jRec.store();

		// when:
		List<Book> books = new BookDAO().loadAllBooks();

		// then:
		assertNotNull(books);
		assertThat(books).hasSize(1);
		Book extracted = books.get(0);
		assertThat(extracted).extracting(Book::getName, Book::getIsbn).containsOnly("Mistrz i Małgorzata", "89898989");
		assertThat(extracted.getId()).isGreaterThan(0);
		List<Author> extractedAuthors = extracted.getAuthors();
		assertThat(extractedAuthors).hasSize(1);
		assertThat(extractedAuthors.get(0)).extracting(Author::getFirstName, Author::getLastName).containsOnly("Michał",
				"Bułchakow");
		assertThat(extractedAuthors.get(0).getId()).isGreaterThan(0);
	}
	
	@Test
	public void test() {
		Book b = new Book(null,"Mistrz i Małgorzata", "89898989");
		BooksRecord bRec = create.newRecord(BOOKS, b);
		bRec.store();
		
		List<Book> books = create.selectFrom(BOOKS).fetchInto(Books.class).stream().map(Book::new).collect(Collectors.toList());
	}
}
