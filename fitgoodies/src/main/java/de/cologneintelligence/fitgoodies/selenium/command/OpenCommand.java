package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

public class OpenCommand extends WrappedCommand {
    public OpenCommand(final String command, final String[] args,
            final CommandProcessor commandProcessor) {
        super(command, args, commandProcessor);
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
