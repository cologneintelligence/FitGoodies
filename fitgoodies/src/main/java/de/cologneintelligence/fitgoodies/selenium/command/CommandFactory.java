package de.cologneintelligence.fitgoodies.selenium.command;


public abstract class CommandFactory {

	public static WrappedCommand createCommand(String command, String[] args) {
		if (command.endsWith("AndRetry")) {
			return new RetryCommand(command, args);	
		} else if ( command.startsWith("open")){
			return new OpenCommand(command, args);
		} else if ( command.startsWith("captureEntirePageScreenshot")){
			return new CaptureEntirePageScreenshotCommand(command, args);
		} else {
			return new SeleniumCommand(command, args);
		}
	    
    }

}
