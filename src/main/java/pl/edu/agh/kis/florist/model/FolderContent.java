package pl.edu.agh.kis.florist.model;

import java.util.List;

/**
 * Created by yevvye on 17.01.17.
 */
public class FolderContent{
    List<FolderModel> foldersList;
    List<FileModel> filesList;

    public FolderContent(List<FolderModel> foldersList, List<FileModel> filesList) {
        this.foldersList = foldersList;
        this.filesList = filesList;
    }

    @Override
    public String toString() {
        return "FolderContent{" +
                "foldersList=" + foldersList +
                ", filesList=" + filesList +
                '}';
    }
}
//    public Object handleCreateNewFolder(Request request, Response response) {
//        String pathDisplay = request.params("path");
//        String pathLower = pathDisplay.toLowerCase();
//        String name = "";
//
//        Scanner folderNameScanner = new Scanner(pathDisplay);
//        //slash (“/”) equivalent to an encoded slash (“%2F”) in the path portion of an HTTP URL
//        folderNameScanner.useDelimiter("%2F");
//
//        // Get the last part of the path which is the folder name
//        while(folderNameScanner.hasNext()) {
//            name = folderNameScanner.next();
//        }
//
//        // Get the local server time and format it
//        Date date = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        String serverCreatedAt = formatter.format(date);
//
//        // TODO what's up with setting folderID (PRIMARY KEY)
//        FolderMetadata folderMetadata = new FolderMetadata(null, name, pathLower, pathDisplay, null, serverCreatedAt, null);
//        //FolderMetadata folderMetadata = gson.fromJson(request.body(), FolderMetadata.class);
//        FolderMetadata result = folderMetadataRepository.storeFolder(folderMetadata);
//        response.status(CREATED);
//        return result;
//    }