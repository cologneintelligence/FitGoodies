package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;

public class SeleniumCommand extends WrappedCommand {

    public SeleniumCommand(final String command, final String[] args,
            final CommandProcessor commandProcessor) {
        super(command, args, commandProcessor);
    }

    @Override
    public String execute() {
        return commandProcessor.doCommand(command, args);
    }

}
