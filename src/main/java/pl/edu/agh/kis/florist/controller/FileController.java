package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.dao.FileDAO;
import pl.edu.agh.kis.florist.db.tables.daos.FileMetadataDao;
import pl.edu.agh.kis.florist.db.tables.daos.FolderMetadataDao;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.*;
import spark.Request;
import spark.Response;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by yevvye on 14.01.2017.
 */

public class FileController {
    private static final int CREATED = 201;

    private final FileDAO fileRepository;
    private final Gson gson = new Gson();

    private FolderMetadataDao folderMetadataDao = new FolderMetadataDao();
    private FileMetadataDao fileMetadataDao = new FileMetadataDao();

    //file controller now can be easily tested
    //and thanks to injection of DataAccessObject objects with constructor
    //can be tested even without database at all - one can stub both interfaces
    //with HashMap-like implementation
    public FileController(FileDAO fileRepository) {
        this.fileRepository = fileRepository;
    }

    //zwraca zawartosc folderu
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

//        String folderPath = request.params("path");
//        FolderModel folder = gson.fromJson(request.body(), FolderModel.class);
//        FolderModel result = fileRepository.createNewFolder(folder,folderPath);
//        response.status(CREATED);
//        return result;

        try{
            FolderModel folderModel = gson.fromJson(request.body(), FolderModel.class);
            Path folderPath = Paths.get(request.params("path").toLowerCase());
            FolderModel result = fileRepository.createNewFolder(folderModel,folderPath);
            response.status(CREATED);
            return result;

//            String folderPath = request.params("path");
//           // FolderModel folderModel = fileRepository.createNewFolder(folderPath);
//            return folderModel;
//            Path p = Paths.get(folderPath); //metody klasy, wyciagniecie ostatniego czlonu
//
//            //FolderMetadataM folderNew = fileRepository.createFolderOfPath(folderPath);
//            fileRepository.createNewFolder(folderModel,p); //parametry
//            return ;//zwrocic nowy folder
        } catch (NumberFormatException ex){
            throw new ParameterFormatException(ex);
        }
    }
}