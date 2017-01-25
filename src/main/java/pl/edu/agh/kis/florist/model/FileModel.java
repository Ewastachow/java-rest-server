package pl.edu.agh.kis.florist.model;
import pl.edu.agh.kis.db.tables.pojos.FileMetadata;

import java.sql.Timestamp;

/**
 * Created by yevvye on 15.01.17.
 */
public class FileModel extends FileMetadata implements FolderFileModel{
    public FileModel(FileMetadata value) {
        super(value);
    }

    public FileModel(Integer fileId, String name, String pathLower, String pathDisplay, Integer size, Timestamp serverCreatedAt, Timestamp serverChangedAt, Integer enclosingFolderId) {
        super(fileId, name, pathLower, pathDisplay, size, serverCreatedAt, serverChangedAt, enclosingFolderId);
    }
}
