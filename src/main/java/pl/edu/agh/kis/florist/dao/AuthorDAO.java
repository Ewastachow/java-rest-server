package pl.edu.agh.kis.florist.dao;

import static pl.edu.agh.kis.florist.db.Tables.AUTHORS;
import static pl.edu.agh.kis.florist.db.Tables.AUTHOR_BOOKS;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import pl.edu.agh.kis.florist.db.tables.records.AuthorsRecord;
import pl.edu.agh.kis.florist.model.Author;

public class AuthorDAO {

	private final String DB_URL = "jdbc:sqlite:test.db";

	public List<Author> loadAuthorsOfBookId(int bookId) {
		try (DSLContext create = DSL.using(DB_URL)) {
			List<Author> authors = 
					create.select(AUTHORS.fields())
						.from(AUTHOR_BOOKS).join(AUTHORS).on(AUTHORS.ID.eq(AUTHOR_BOOKS.AUTHOR_ID))
						.where(AUTHOR_BOOKS.BOOK_ID.equal(bookId)).fetchInto(Author.class);
			return authors;
		}
	}
	
	public List<Author> loadAllAuthors() {
		try (DSLContext create = DSL.using(DB_URL)) {
			List<Author> authors = 
					create.select(AUTHORS.fields())
						.from(AUTHORS)
						.fetchInto(Author.class);
			return authors;
		}
	}
	
	public Author store(Author author) {
		try (DSLContext create = DSL.using(DB_URL)) {
			AuthorsRecord record = create.newRecord(AUTHORS,author);
			record.store();
			return record.into(Author.class);
		}
	}

	public List<Author> store(List<Author> authors) {
		//in the future can be optimized into single db query
		return authors.stream().map(this::store).collect(Collectors.toList());
	}
	
	
}
