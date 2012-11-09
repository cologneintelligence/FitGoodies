package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public abstract class CommandFactory {
    public static WrappedCommand createCommand(final String command, final String[] args, final SetupHelper helper) {
        if (command.endsWith("AndRetry")) {
            return new RetryCommand(command, args, helper);
        } else if (command.startsWith("open")){
            return new OpenCommand(command, args, helper.getCommandProcessor());
        } else if (command.startsWith("captureEntirePageScreenshot")){
            return new CaptureEntirePageScreenshotCommand(command, args, helper);
        } else {
            return new SeleniumCommand(command, args, helper.getCommandProcessor());
        }
    }
}
