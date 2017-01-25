package pl.edu.agh.kis.florist.exceptions;

public class ParameterFormatException extends RuntimeException {

	public ParameterFormatException(NumberFormatException ex) {
		super(ex);
	}

}
