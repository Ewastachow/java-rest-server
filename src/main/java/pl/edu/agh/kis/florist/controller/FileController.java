package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import pl.edu.agh.kis.florist.db.tables.daos.FileContentsDao;
import pl.edu.agh.kis.florist.db.tables.daos.FileMetadataDao;
import pl.edu.agh.kis.florist.db.tables.daos.FolderMetadataDao;
import pl.edu.agh.kis.florist.db.tables.pojos.FileContents;
import pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;
import pl.edu.agh.kis.florist.exceptions.InvalidPathException;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.*;
import spark.Request;
import spark.Response;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yevvye on 14.01.2017.
 */

public class FileController {
    private final String DB_URL = "jdbc:sqlite:test.db";
    private static final int CREATED = 201;

    private final Gson gson = new Gson();

    private Connection connection;
    private Configuration configuration;

    private FileMetadataDao fileMetadataDao;
    private FolderMetadataDao folderMetadataDao;
    private FileContentsDao fileContentsDao;

    public FileController() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        configuration = new DefaultConfiguration().set(connection).set(SQLDialect.SQLITE);
        fileMetadataDao = new FileMetadataDao(configuration);
        folderMetadataDao = new FolderMetadataDao(configuration);
        fileContentsDao = new FileContentsDao(configuration);

    }

    public Object handleFolderContent(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        List<FileMetadata> files = new ArrayList<>();
        List<FolderMetadata> folders = new ArrayList<>();
        try{
            FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
            folders.addAll(folderMetadataDao.fetchByParentFolderId(folder.getFolderId()));
            files.addAll(fileMetadataDao.fetchByParentFolderId(folder.getFolderId()));
            List<List> result = new ArrayList<>();
            result.add(files);
            result.add(folders);
            return result;
        }catch(Exception e) {
            throw new InvalidPathException(path.toString()+" not exist");
        }
    }

    public Object handleFolderData(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        FolderMetadata result = null;
        try{
            result = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
            return result;
        }catch(Exception e){
            try{
                FileMetadata foundedFile = null;
                foundedFile = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
                return foundedFile;
            }catch (Exception ex){
                throw new InvalidPathException(path.toString()+" not found");
            }
        }
    }

    public Object handleDeleteFolder(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        try{
            FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
            List<FolderMetadata> folders = getListOfAllFoldersInside(folder.getFolderId());
            List<FileMetadata> files = getListOfAllFilesInListOfFolders(folders, folder.getFolderId());
            for(FileMetadata i : files){
                FileContents fileContents = fileContentsDao.fetchOneByFileId(i.getFileId());
                fileContentsDao.delete(fileContents);
                fileMetadataDao.delete(i);
            }
            for(FolderMetadata i : folders) folderMetadataDao.delete(i);
            folderMetadataDao.delete(folder);
            return folder;
        }catch (Exception e){
            try{
                FileMetadata file = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
                FileContents fileContents = fileContentsDao.fetchOneByFileId(file.getFileId());
                fileContentsDao.delete(fileContents);
                fileMetadataDao.delete(file);
                return file;
            }catch (Exception ex){
                throw new InvalidPathException(path.toString()+" not exist");
            }
        }
    }

    public Object handleMoveFolder(Request request, Response response) { //// TODO: 25.01.17 Zaimplementować - nie ma nic :<<
        Path path = Paths.get(request.params("path"));
        Path newPath = Paths.get(request.queryParams("new_path"));
        int toChange = path.getNameCount();

        try{
            FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);

            List<FolderMetadata> folders = getListOfAllFoldersInside(folder.getFolderId());
            List<FileMetadata> files = getListOfAllFilesInListOfFolders(folders, folder.getFolderId());

            folderMetadataDao.delete(folder);
            FolderMetadata newFolder = new FolderMetadata(folder.getFolderId(), folder.getName(),
                    newPath.toString()+"/"+folder.getName(), newPath.toString()+"/"+folder.getName(), folder.getParentFolderId(), folder.getServerCreatedAt());
            folderMetadataDao.insert(newFolder);

            for(FolderMetadata i: folders){
                Path oldPath = Paths.get(i.getPathLower());
                String newIPathString = newPath.toString()+"/"+oldPath.subpath(toChange-1,oldPath.getNameCount());

                folderMetadataDao.delete(i);
                FolderMetadata newI = new FolderMetadata(i.getFolderId(), i.getName(),
                        newIPathString, newIPathString, i.getParentFolderId(), i.getServerCreatedAt());
                folderMetadataDao.insert(newI);
            }
            for(FileMetadata i : files){
                Path oldPath = Paths.get(i.getPathLower());
                String newIPathString = newPath.toString()+"/"+oldPath.subpath(toChange-1,oldPath.getNameCount());

                Timestamp time = new Timestamp(System.currentTimeMillis());

                fileMetadataDao.delete(i);
                FileMetadata newI = new FileMetadata(i.getFileId(), i.getName(),
                        newIPathString, newIPathString, i.getParentFolderId(), i.getSize(), i.getServerCreatedAt(), time, i.getEnclosingFolderId());
                fileMetadataDao.insert(newI);
            }
            return newFolder;

        }catch(Exception e){
            throw new InvalidPathException(path.toString()+" nope");
        }
    }

    public Object handleCreateFolder(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        FolderMetadata parent;
        Path parentPath = path.getParent();
        if (parentPath != null) {
            try{
                FolderMetadata tested = folderMetadataDao.fetchByPathLower(path.getParent().toString()).get(0);
            }catch(Exception e){
                throw new InvalidPathException(path.toString()+" cant exist");
            }
            String lowerPath = parentPath.toString().toLowerCase();
            parent = folderMetadataDao.fetchByPathLower(lowerPath).get(0);
            Timestamp time = new Timestamp(System.currentTimeMillis());
            FolderMetadata folder = new FolderMetadata(null, path.getFileName().toString(),
                    path.toString().toLowerCase(), path.toString(), parent.getFolderId(), time);
            folderMetadataDao.insert(folder);
            FolderMetadata result = folderMetadataDao.fetchByPathLower(path.toString().toLowerCase()).get(0);
            response.status(CREATED);
            return result;
        } else {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            FolderMetadata folder = new FolderMetadata(null, path.getFileName().toString(),
                    path.toString().toLowerCase(), path.toString(), null, time);
            folderMetadataDao.insert(folder);
            FolderMetadata result = folderMetadataDao.fetchByPathLower(path.toString().toLowerCase()).get(0);
            response.status(CREATED);
            return result;
        }
    }

    public Object handlePostFile(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        String content = request.body();    //// TODO: 25.01.17 Chyba zawartość pliku jest źle czytana
        FileMetadata file;
        FolderMetadata parent;
        FileMetadata result;
        Path parentPath = path.getParent();
        if (parentPath != null) {
            try{
                FolderMetadata tested = folderMetadataDao.fetchByPathLower(path.getParent().toString()).get(0);
            }catch(Exception e){
                throw new InvalidPathException(path.toString()+" cant exist");
            }
            String lowerPath = parentPath.toString().toLowerCase();
            parent = folderMetadataDao.fetchByPathLower(lowerPath).get(0);
            Timestamp time = new Timestamp(System.currentTimeMillis());
            file = new FileMetadata(null, path.getFileName().toString(),
                    path.toString().toLowerCase(), path.toString(), parent.getFolderId(), content.length(), time, time, parent.getFolderId());
            fileMetadataDao.insert(file);
            result = fileMetadataDao.fetchByPathLower(path.toString().toLowerCase()).get(0);
        } else {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            file = new FileMetadata(null, path.getFileName().toString(),
                    path.toString().toLowerCase(), path.toString(), null, content.length(), time, time, null);
            fileMetadataDao.insert(file);
            result = fileMetadataDao.fetchByPathLower(path.toString().toLowerCase()).get(0);
        }
        FileContents fileContents = new FileContents(file.getFileId(),content.getBytes());
        fileContentsDao.insert(fileContents);
        response.status(CREATED);
        return result;
    }

    public Object handleRenameFolder(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        String newName = request.queryParams("new_name");
        try{
            FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
            //// TODO: 25.01.17 implement rename for folders -- not necessery

        }catch(Exception e){
            try{
                FileMetadata file = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
                fileMetadataDao.delete(file);

                Path oldPath = Paths.get(file.getPathLower());
                String newPath = oldPath.getParent()+"/"+newName;

                Timestamp time = new Timestamp(System.currentTimeMillis());
                FileMetadata newFile = new FileMetadata(file.getFileId(), newName, newPath,
                        newPath, file.getParentFolderId(), file.getSize(), file.getServerCreatedAt(), time, file.getEnclosingFolderId());

                fileMetadataDao.insert(newFile);
                response.status(CREATED);
                return newFile;

            }catch(Exception ex){
                throw new InvalidPathException(path.toString()+" not exist");
            }

        }
        return null;
    }

    public Object handleDownloadFile(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        try{
            FileMetadata file = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
            FileContents fileContents = fileContentsDao.fetchOneByFileId(file.getFileId());

            return fileContents.getContents();//// TODO: 25.01.17 Czy to jest poprawne???

        }catch(Exception e){
            throw new InvalidPathException(path.toString()+" file not exist");
        }
    }

    private List<FolderMetadata> getListOfAllFoldersInside(int folderId){
        List<FolderMetadata> list = new ArrayList<>();
        List<FolderMetadata> folders = new ArrayList<>();
        folders.addAll(folderMetadataDao.fetchByParentFolderId(folderId));
        list.addAll(folders);
        for(FolderMetadata i : folders){
            list.addAll(getListOfAllFoldersInside(i.getFolderId()));
        }
        return list;
    }

    private List<FileMetadata> getListOfAllFilesInListOfFolders(List<FolderMetadata> folders, int folderId){
        List<FileMetadata> list = new ArrayList<>();
        for(FolderMetadata i : folders){
            list.addAll(fileMetadataDao.fetchByParentFolderId(i.getFolderId()));
        }
        list.addAll(fileMetadataDao.fetchByParentFolderId(folderId));
        return list;
    }
}