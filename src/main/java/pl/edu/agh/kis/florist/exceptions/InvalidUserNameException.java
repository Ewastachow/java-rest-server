package pl.edu.agh.kis.florist.exceptions;

/**
 * Created by yevvye on 26.01.17.
 */
public class InvalidUserNameException extends RuntimeException {

    public InvalidUserNameException(NumberFormatException ex) {
        super(ex);
    }

}