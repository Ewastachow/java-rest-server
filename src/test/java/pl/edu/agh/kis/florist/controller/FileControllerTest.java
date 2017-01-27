package pl.edu.agh.kis.florist.controller;

import static pl.edu.agh.kis.florist.db.Tables.*;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;

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
	public void createListOfFolders() {
		FileController fileController = new FileController();

		FolderMetadata folder = new FolderMetadata(1, "lama", "lama", "lama", null, null);

		fileController.folderMetadataDao.insert(folder );
		fileController.folderMetadataDao.insert( new FolderMetadata(2, "to", "lama/to", "lama/to", 1, null));

		String string = "[[],[{'folderId':4,'name':'alpaka','pathLower':'aloes/alpaka',}]]";
		assertFalse("",(fileController.getListOfAllFoldersInside(folder.getFolderId())).equals(string));

	}

	@Test
    public void createFolderList(){
//        FileController tested = new FileController();
//        tested.
    }

}