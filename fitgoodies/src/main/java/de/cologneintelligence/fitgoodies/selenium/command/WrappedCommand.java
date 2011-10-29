package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public abstract class WrappedCommand {
	protected final String command;
	protected final String[] args;
	protected final CommandProcessor commandProcessor;

	
	public WrappedCommand(String command, String[] args) {
	    super();
	    this.command = command;
	    this.args = args;
	    this.commandProcessor = SetupHelper.instance().getCommandProcessor();
    }

	public abstract String execute();
	

}
