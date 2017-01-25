package pl.edu.agh.kis.florist.dao;

/**
 * Created by yevvye on 14.01.17.
 */
public class  FolderDAO {
    private final String DB_URL = "jdbc:sqlite:test.db";

    /*public FolderModel loadFolderOfId(int folderId) {
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.FOLDER_ID.equal(folderId)).fetchOne();
            FolderModel folderModel = record.into(FolderModel.class);
            List<FolderContent> folderContent = loadFolderContentOfFolderId(folderId);
            return folderModel.withFolderContent(folderContent);
        }
    }*/

    /*public List<FolderContent> loadFolderContentOfFolderId(int folderId) {
        try (DSLContext create = DSL.using(DB_URL)) {
            List<FolderContent> folderContent =
                    create.select(FOLDER_METADATA.fields())
                            .from(FOLDER_FOLDER_CONTENTS).join(FOLDER_METADATA).on(FOLDER_METADATA.FOLDER_ID.eq(FOLDER_FOLDER_CONTENTS.CONTAINED_FOLDER_ID))
                            .where(FOLDER_FOLDER_CONTENTS.PARENT_FOLDER_ID.equal(folderId)).fetchInto(FolderModel.class);
            folderContent.addAll(create.select(FILE_METADATA.fields())
                    .from(FOLDER_FILE_CONTENTS).join(FILE_METADATA).on(FILE_METADATA.FILE_ID.eq(FOLDER_FILE_CONTENTS.CONTAINED_FILE_ID))
                    .where(FOLDER_FILE_CONTENTS.PARENT_FOLDER_ID.equal(folderId)).fetchInto(FileModel.class));
            return folderContent;
        }
    }*/
}
