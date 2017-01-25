package pl.edu.agh.kis.florist.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata;

import java.sql.Timestamp;

/**
 * Created by yevvye on 15.01.17.
 */
public class FileModel extends FileMetadata implements FolderFileModel{
    public FileModel(FileMetadata value) {
        super(value);
    }


}
