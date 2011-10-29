package de.cologneintelligence.fitgoodies.selenium.command;


public class SeleniumCommand extends WrappedCommand {

	public SeleniumCommand(String command, String[] args) {
	    super(command, args);
    }

	@Override
	public String execute() {
		return commandProcessor.doCommand(command, args);
	}

}
