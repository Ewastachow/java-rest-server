package pl.edu.agh.kis.florist.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static pl.edu.agh.kis.florist.db.Tables.AUTHORS;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.kis.florist.db.tables.records.AuthorsRecord;

public class AuthorDAOTest {

	private final String DB_URL = "jdbc:sqlite:test.db";
	private DSLContext create;
	
	@Before
	public void setUp() {
		//establish connection with database
		create = DSL.using(DB_URL);
		//clean all data before every test
		create.deleteFrom(AUTHORS).execute();
	}
	
	@After
	public void tearDown() {
		create.close();
	}

	@Test
	public void createNewAuthorInDatabaseAndCanLoadIt() {
	  //setup:
		Author a = new Author("Michał","Bułchakow");
		AuthorsRecord rec = create.newRecord(AUTHORS,a);
		rec.store();
		
		// when:
		List<Author> authors = new AuthorDAO().loadAllAuthors();
		
	  //then:
		assertThat(authors).hasSize(1).extracting(Author::getFirstName).containsOnly("Michał");
	}
	
	@Test
	public void storingDataWithRepositoryReturnsAuthorWithId() {
	  //setup:
		Author a = new Author("Michał","Bułchakow");
		
	  //when:	
	    Author author = new AuthorDAO().store(a);
		
	  //then:
	    assertNotNull(author);
		assertThat(author.getId()).isGreaterThan(0);
		
		AuthorsRecord rec = create.selectFrom(AUTHORS).where(AUTHORS.ID.eq(author.getId())).fetchOne();
		assertThat(rec).extracting(AuthorsRecord::getFirstName,AuthorsRecord::getLastName).containsOnly("Michał","Bułchakow");
	}
}
