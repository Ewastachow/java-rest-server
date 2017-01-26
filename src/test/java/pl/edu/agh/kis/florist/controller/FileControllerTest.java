package pl.edu.agh.kis.florist.controller;

import static pl.edu.agh.kis.florist.db.Tables.*;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yevvye on 26.01.17.
 */
public class FileControllerTest {

    private final String DB_URL = "jdbc:sqlite:test.db";
	private DSLContext create;

	@Before
	public void setUp() {
		create = DSL.using(DB_URL);
		create.deleteFrom(FILE_METADATA).execute();
        create.deleteFrom(FILE_CONTENTS).execute();
        create.deleteFrom(FOLDER_METADATA).execute();
	}

	@After
	public void tearDown() {
		create.close();
	}

    @Test
	public void createAndLoadNewFolder() {
	  //setup:
//		Fo a = new Author("Michał","Bułchakow");
//		AuthorsRecord rec = create.newRecord(AUTHORS,a);
//		rec.store();
//
//		// when:
//		List<Author> authors = new AuthorDAO().loadAllAuthors();
//
//	  //then:
//		assertThat(authors).hasSize(1).extracting(Author::getFirstName).containsOnly("Michał");
	}

	@Test
    public void createFolderList(){
        FileController tested = new FileController();
//        tested.
    }

}