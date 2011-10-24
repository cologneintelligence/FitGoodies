package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.Wait;

public abstract class Retry extends Wait {
	private String command;
	private String[] args;
	private boolean result;
	private int counter;
	private final Long timeoutInMilliseconds;
	private final Long intervalInMilliseconds;	
	
	public boolean wasOk() {
		return result;
	}
	
	public int getCounter() {
		return counter;
	}

	public String attemptMessage() {
		return " attempts: " + getCounter() +"/" + timeoutInMilliseconds/intervalInMilliseconds 	+ " times";
	}


	public Retry(String command, String[] args, Long timeout, Long interval) {
		this.command = command;
		this.args = args;
		this.timeoutInMilliseconds = timeout;
		this.intervalInMilliseconds = interval;
	}

	@Override
	public boolean until() {
		try {
			counter++;
			result = command(command, args);
		} catch (SeleniumException e) {
			result = false;
		}
		return result;
	}

	public void start(String message) {
		wait(message, timeoutInMilliseconds, intervalInMilliseconds);
	}
	
	public abstract boolean command(String command2, String[] args2);
}
