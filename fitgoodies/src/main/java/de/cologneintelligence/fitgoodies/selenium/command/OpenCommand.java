package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.SeleniumException;

public class OpenCommand extends WrappedCommand {


	public OpenCommand(String command, String[] args) {
	    super(command, args);
    }

	@Override
	public String execute() {
		String returnValue;
		try {
			returnValue = commandProcessor.doCommand(command, args);
		} catch (final SeleniumException e) {
			returnValue = commandProcessor.doCommand("waitForPageToLoad", new String[] { "50000", });
		}
		return returnValue;
	}

}
