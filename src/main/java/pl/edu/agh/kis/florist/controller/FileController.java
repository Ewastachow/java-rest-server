package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import pl.edu.agh.kis.florist.dao.FileDAO;
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

    private final FileDAO fileRepository;
    private final Gson gson = new Gson();

    private Connection connection;
    private Configuration configuration;

    private FileMetadataDao fileMetadataDao;
    private FolderMetadataDao folderMetadataDao;
    private FileContentsDao fileContentsDao;

    public FileController(FileDAO fileRepository) {
        this.fileRepository = fileRepository;
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

    public Object handleFolderContent(Request request, Response response) {//// TODO: 25.01.17 Wyrzucanie wyjątku gdy szukany folder nie istnieje
        Path path = Paths.get(request.params("path"));
        List<FileMetadata> files = new ArrayList<>();
        List<FolderMetadata> folders = new ArrayList<>();
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        folders.addAll(folderMetadataDao.fetchByParentFolderId(folder.getFolderId()));
        files.addAll(fileMetadataDao.fetchByParentFolderId(folder.getFolderId()));
        List<List> result = new ArrayList<>();
        result.add(files);
        result.add(folders);
       return result;
    }

    public Object handleFolderData(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        FolderMetadata result = null;
        result = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        if(result==null){ //// TODO: 25.01.17 Nie sprawdzone czy działa dla plików
            List<FileMetadata> foundedFile = null;
            foundedFile = fileMetadataDao.fetchByPathLower(path.getParent().toString());
            for(FileMetadata i : foundedFile){
                if(path.getFileName().toString().equals(i.getName())){
                    return i;
                }
            }
            throw new InvalidPathException(path.toString()+" not found");
        }
        return result;
    }

    public Object handleDeleteFolder(Request request, Response response) {//// TODO: 25.01.17 Narazie działa tylko dla folderów, i nawet nie sprawdza czy to plik i musi usuwać szystko szystko co jest w środku,
        Path path = Paths.get(request.params("path"));
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        folderMetadataDao.delete(folder);
        return folder;
    }

    public Object handleMoveFolder(Request request, Response response) { //// TODO: 25.01.17 Zaimplementować - nie ma nic :<<
        Path path = Paths.get(request.params("path"));
        String newPath = request.queryParams("new_path");
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);

        return null;
    }

    public Object handleCreateFolder(Request request, Response response) {
        Path path = Paths.get(request.params("path"));
        FolderMetadata parent;
        Path parentPath = path.getParent();
        if (parentPath != null) {
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
            String lowerPath = parentPath.toString().toLowerCase();
            parent = folderMetadataDao.fetchByPathLower(lowerPath).get(0);
            Timestamp time = new Timestamp(System.currentTimeMillis());//// TODO: 25.01.17 nazwa pliku jest źle, powinna być jakoś z body brana
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

    public Object handleRenameFolder(Request request, Response response) {//// TODO: 25.01.17 everything
        Path path = Paths.get(request.params("path"));
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        if(folder==null){
            FileMetadata file = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
        }
        String newName = request.queryParams("new_name");
        return null;
    }

    private List<Object> getListOfAllObjectInside(List<Object> list, int folderId){
        List<FolderMetadata> folders = new ArrayList<>();
        folders.addAll(folderMetadataDao.fetchByParentFolderId(folderId));
        list.addAll(fileMetadataDao.fetchByParentFolderId(folderId));
        list.addAll(folders);
        for(FolderMetadata i : folders){
            getListOfAllObjectInside(list, i.getFolderId());
        }
        return list;
    }
 }