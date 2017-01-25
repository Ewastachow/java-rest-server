package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import pl.edu.agh.kis.florist.dao.FileDAO;
import pl.edu.agh.kis.florist.db.tables.daos.FileMetadataDao;
import pl.edu.agh.kis.florist.db.tables.daos.FolderMetadataDao;
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

    }

    public Object handleFolderContent(Request request, Response response) {//// TODO: 25.01.17 Wyrzucanie wyjątku gdy szukany folder nie istnieje
        Path path = Paths.get(request.params("path"));
        List<FileMetadata> files = new ArrayList<>();
        List<FolderMetadata> folders = new ArrayList<>();
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        folders.addAll(folderMetadataDao.fetchByParentFolderId(folder.getFolderId()));
        files.addAll(fileMetadataDao.fetchByPathLower(path.toString()));
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
            FileMetadata foundedFile = null;
            foundedFile = fileMetadataDao.fetchByPathLower(path.toString()).get(0);
            if(foundedFile==null){
                throw new InvalidPathException(path.toString()+" not found");
            }
        }
        return result;
    }

    public Object handleDeleteFolder(Request request, Response response) {//// TODO: 25.01.17 Narazie działa tylko dla folderów, i nawet nie sprawdza czy to plik,  
        Path path = Paths.get(request.params("path"));
        FolderMetadata folder = folderMetadataDao.fetchByPathLower(path.toString()).get(0);
        folderMetadataDao.delete(folder);
        return folder;
    }

    public Object handleMoveFolder(Request request, Response response) {
        /*try{
            //gdzie przeniesc ten folder??
            String folderPath = request.params("path");
            String newPath = request.queryParams("new_path");
            return null; //todo
        } catch (NumberFormatException ex){
            throw new ParameterFormatException(ex);
        }*/
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
}