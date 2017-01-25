package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import pl.edu.agh.kis.florist.dao.FileDAO;
import pl.edu.agh.kis.florist.db.tables.daos.FileMetadataDao;
import pl.edu.agh.kis.florist.db.tables.daos.FolderMetadataDao;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;
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

    public Object handleFolderContent(Request request, Response response) {
//        try {
//            String folderPath = request.params("path");
//            // uzywamy repozytorium, zeby znalezc containedFileId albo containedFolderId
//            //zwraca obiekt klasy FolderFolderContentsM albo FolderFileContentsM ? a co jak sa oba?
//            if (!folderPath.startsWith("/")) {
//                throw new InvalidPathException(folderPath);
//            }
//
//            List<FolderMetadata> folderMetadatas = folderMetadataDao.fetchByPathLower(folderPath);
//            if (folderMetadatas.size()==0) {
//                //throw new FolderDoesNotExistException(folderPath); //stworzyc wyjatek
//            }
//            FolderMetadata rootFolder = folderMetadatas.get(0); //jesli jest kilka folderow, size>1, blad
//            Collection<FolderMetadata> folderList = folderMetadataDao.fetchByParentFolderId(rootFolder.getFolderId());
//            Collection<FileMetadata> fileList = fileMetadataDao.fetchByEnclosingFolderId(rootFolder.getFolderId());
//            return new FolderContent(folderList,fileList);
//        } catch (NumberFormatException ex) {
//            throw new ParameterFormatException(ex);
//        }
       return null;
    }

    public Object handleFolderData(Request request, Response response) {
        try {
            String folderPath = request.params("path");
            FolderFileModel folderModel = fileRepository.loadFolderDataOfPath(folderPath);
            return folderModel;
        } catch (NumberFormatException ex) {
            throw new ParameterFormatException(ex);
        }
//        return null;
    }

    public Object handleDeleteFolder(Request request, Response response) { //czy ma zwracac kod bledu?
        /*try{
            String folderPath = request.params("path");
            int kod = fileRepository.deleteFolderOfPath(folderPath);
            return kod;
        } catch (NumberFormatException ex){
            throw new ParameterFormatException(ex);
        }*/
        return null;
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