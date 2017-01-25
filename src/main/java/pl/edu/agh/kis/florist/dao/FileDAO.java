package pl.edu.agh.kis.florist.dao;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.florist.db.tables.daos.FileMetadataDao;
import pl.edu.agh.kis.florist.db.tables.daos.FolderMetadataDao;
import pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;
import pl.edu.agh.kis.florist.exceptions.InvalidPathException;
import pl.edu.agh.kis.florist.model.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;
import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;


/**
 * Created by yevvye on 16.01.17.
 */
public class FileDAO {
    private final String DB_URL = "jdbc:sqlite:test.db";
    FileMetadataDao generatedFileDAO = new FileMetadataDao();
    FolderMetadataDao generatedFolderDAO = new FolderMetadataDao();


    public FolderFileModel loadFolderDataOfPath(String folderPath) {
//        FolderFileModel finded;
//        Path path = Paths.get(folderPath);
//        FolderModel currentFolder = null;
//        int currentFolderId = 0;
//        for(int i=0; i<(path.getNameCount()-1); i++){
//            List<FolderModel> folders = getFolderContent(currentFolderId);
//            for(FolderModel j : folders)
//                if(path.getName(i).equals(j.getName()))
//                    currentFolderId = j.getFolderId();
//        }
//        int loadedFolderId=0;
//
//        List<FolderModel> folders = getFolderContent(currentFolderId);
//        for(FolderModel i : folders)
//            if(path.getName(path.getNameCount()-1).equals(i.getName()))
//                loadedFolderId = i.getFolderId();
//        if(loadedFolderId!=0){
//            try (DSLContext create = DSL.using(DB_URL)) {
//                FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.FOLDER_ID.equal(loadedFolderId)).fetchOne();
//                finded = record.into(FolderModel.class);
//            }
//        }else{
//            List<FileModel> files = getFileContent(currentFolderId);
//            for(FileModel i : files)
//                if(path.getName(path.getNameCount()-1).equals(i.getName()))
//                    loadedFolderId = i.getFileId();
//
//            try (DSLContext create = DSL.using(DB_URL)) {
//                FileMetadataRecord record = create.selectFrom(FILE_METADATA).where(FILE_METADATA.FILE_ID.equal(loadedFolderId)).fetchOne();
//                finded = record.into(FileModel.class);
//            }
//        }

        Path path = Paths.get(folderPath);
        String fileName = path.getFileName().toString();
        String[] ifFile = fileName.split(".");
        if(ifFile.length!=1){
            List<FileMetadata> whereLook =
                    generatedFileDAO.fetchByPathLower(path.subpath(0,
                            path.getNameCount()-1).toString());
            for(FileMetadata i : whereLook){
                if(i.getName().equals(path.getFileName().toString()))
                    return (FileModel) i;
            }
        }else{
            List<FolderMetadata> whereLook =
                    generatedFolderDAO.fetchByPathLower(path.subpath(0,
                            path.getNameCount()-1).toString());
            for(FolderMetadata i : whereLook){
                if(i.getName().equals(path.getFileName().toString()))
                    return (FolderModel) i;
            }
        }
        throw new InvalidPathException(folderPath+" not found");

    }

    private List<FolderModel> getFolderContent(int parentFolderId){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<FolderModel> folders =
                    create.select(FOLDER_METADATA.fields())
                            .where(FOLDER_METADATA.PARENT_FOLDER_ID.equal(parentFolderId)).fetchInto(FolderModel.class);
            return folders;
        }
    }

    private List<FileModel> getFileContent(int parentFolderId){
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.FOLDER_ID.equal(parentFolderId)).fetchOne();
            FolderModel folderModel = record.into(FolderModel.class);
            String path = folderModel.getPathLower();
            path = path+"/"+folderModel.getName();
            List<FileModel> files =
                    create.select(FILE_METADATA.fields())
                            .where(FILE_METADATA.PATH_LOWER.equal(path)).fetchInto(FileModel.class);
            return files;
        }
    }

    public FolderModel createNewFolder(FolderModel folderModel, Path path) {
        int parentFolderId = 0;
        String tmp;
        try {
            tmp = path.getParent().toString();
        }catch(Exception e){
            tmp = "";
        }
        String tmp2 = "fsdf";

        try (DSLContext create = DSL.using(DB_URL)) {
            List<FolderModel> whereLook =
                    create.select(FOLDER_METADATA.fields())
                            .where(FOLDER_METADATA.PATH_LOWER.equal(tmp)).fetchInto(FolderModel.class);
            for(FolderMetadata i : whereLook){
                if(i.getName().equals(path.getFileName().toString()))
                    parentFolderId = i.getFolderId();
            }
        }
//        List<FolderMetadata> whereLook =
//                generatedFolderDAO.fetchByPathLower(tmp);
//        for(FolderMetadata i : whereLook){
//            if(i.getName().equals(path.getFileName().toString()))
//                parentFolderId = i.getFolderId();
//        }

//        FolderModel folder = new FolderModel(folderModel.getFolderId(),
//                folderPath, folderModel.getPathLower(), folderModel.getPathDisplay(),
//                parentFolderId, folderModel.getServerCreatedAt());

        /*if(path.getNameCount()==0){ //// TODO: 22.01.17 Zmienić parentfolderid
//            folder = new FolderMetadata(null,path.getFileName().toString(),"","",parentFolderId,null);//// TODO: 20.01.17 Co z autoinkrementacją id???
        }else if(path.getNameCount()==1){
            List<FolderMetadata> whereLook = generatedFolderDAO.fetchByParentFolderId(0);
            for(FolderMetadata i : whereLook){
                if(i.getName().equals(path.getFileName().toString()))
                    parentFolderId = i.getFolderId();
            }
            folder = new FolderMetadata(null,path.getFileName().toString(),path.getParent().toString(),path.getParent().toString(),parentFolderId,null);//// TODO: 20.01.17 Co z autoinkrementacją id???

        }else if(path.getNameCount()>1){
            List<FolderMetadata> whereLook =
                    generatedFolderDAO.fetchByPathLower(path.subpath(0,
                            path.getNameCount()-2).toString());
            for(FolderMetadata i : whereLook){
                if(i.getName().equals(path.getFileName().toString()))
                    parentFolderId = i.getFolderId();
            }
            folder = new FolderMetadata(null,path.getFileName().toString(),path.getParent().toString(),path.getParent().toString(),parentFolderId,null);//// TODO: 20.01.17 Co z autoinkrementacją id???

        }else throw new InvalidPathException("Impossible");*/

//        try (DSLContext create = DSL.using(DB_URL)) {
//            FolderMetadataRecord record = create.newRecord(FOLDER_METADATA,folder);
//            record.store();
//            return record.into(FolderModel.class);
//        }

        //generatedFolderDAO.insert(folder);
        //return (FolderModel) folder;
//        String fileName = path.getFileName().toString();
        return null;
    }
}
