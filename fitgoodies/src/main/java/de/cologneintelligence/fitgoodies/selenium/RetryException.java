package de.cologneintelligence.fitgoodies.selenium;

public class RetryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RetryException(String message) {
		super(message);
	}

}
