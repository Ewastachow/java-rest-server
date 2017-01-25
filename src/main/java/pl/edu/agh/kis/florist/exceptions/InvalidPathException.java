package pl.edu.agh.kis.florist.exceptions;

/**
 * Created by yevvye on 18.01.2017.
 */
public class InvalidPathException extends RuntimeException {
    private String folderPath;

    public InvalidPathException(String folderPath) {

        this.folderPath = folderPath;
    }
}