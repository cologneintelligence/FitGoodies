package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;

public abstract class WrappedCommand {
    protected final String command;
    protected final String[] args;
    protected final CommandProcessor commandProcessor;

    public WrappedCommand(final String command, final String[] args,
            final CommandProcessor commandProcessor) {
        this.command = command;
        this.args = args;
        this.commandProcessor = commandProcessor;
    }

    public abstract String execute();


}
