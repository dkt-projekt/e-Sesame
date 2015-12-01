package eu.freme.broker.esesame.exceptions;

public class ExternalServiceFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExternalServiceFailedException(String message) {
		super(message);
	}
}
