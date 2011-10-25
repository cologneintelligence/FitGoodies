package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.SeleniumException;

public class RetryException extends SeleniumException {
	private static final long serialVersionUID = 1L;

	public RetryException(String message) {
		super(message);
	}

}
