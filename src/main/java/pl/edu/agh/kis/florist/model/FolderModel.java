package pl.edu.agh.kis.florist.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by yevvye on 14.01.17.
 */
public class FolderModel extends FolderMetadata implements FolderFileModel{

    private List<FolderModel> foldersInFolder;
    private List<FileModel> filesInFolder;

    public FolderModel(FolderMetadata value) {
        super(value);
    }

    public FolderModel(Integer folderId, String name, String pathLower, String pathDisplay, Integer parentFolderId, Timestamp serverCreatedAt) {
        super(folderId, name, pathLower, pathDisplay, parentFolderId, serverCreatedAt);
    }


    /*public List<FolderMetadata> getFolders() {
        return foldersInFolder;
    }
    public List<FileMetadata> getFiles() {
        return filesInFolder;
    }

    public FolderModel withFolderContent(List<FolderContent> folderContent) {
        if (this.folderContent != null && !this.folderContent.isEmpty()) {
            throw new IllegalStateException("this book already has authors!");
        }
        return new FolderModel(getFolderId(), getName(), getPathLower(), getPathDisplay(), getParentFolderId(), getServerCreatedAt(), getOwnerId(), folderContent);
    }*/
}
