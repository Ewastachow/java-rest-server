package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;
import pl.edu.agh.kis.florist.dao.FolderDAO;

/**
 * Created by yevvye on 14.01.17.
 */
public class FolderController {

    private final FolderDAO folderRepository;
    private final Gson gson = new Gson();

    public FolderController(FolderDAO folderRepository) {
        this.folderRepository = folderRepository;
    }

    /*public Object handleSingleFolder(Request request, Response response) {
        try {
            int folderId = Integer.parseInt(request.params("folderid"));
            FolderModel result = folderRepository.loadFolderOfId(folderId);
            return result;
            //z tym catch trzeba coś zrobić
        } catch (NumberFormatException ex) {
            throw new ParameterFormatException(ex);
        }
    }*/
}
