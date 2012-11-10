package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.selenium.Retry;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;



public class RetryCommand extends WrappedCommand {
    private final SetupHelper helper;

    public RetryCommand(final String command, final String[] args, final SetupHelper helper) {
        super(command, args, helper.getCommandProcessor());
        this.helper = helper;
    }

    @Override
    public String execute() {
        final Retry retry = new Retry(helper.getTimeout(), helper.getInterval()) {
            private String result;
            @Override
            public boolean execute() {
                try{
                    final String seleniumCommand = command.substring(0, command.indexOf("AndRetry"));
                    result = commandProcessor.doCommand(seleniumCommand, args);
                } catch (final SeleniumException e) {
                    throw new RetryException(e.getMessage() + "; " + attemptMessage(attemptCounter()));
                }
                return result.startsWith("OK");
            }
        };
        return (retry.start() ? "OK; " : "NOK; ") + attemptMessage(retry.attemptCounter());
    }

    private String attemptMessage(final int count) {
        return "attempts: " + count + " times";
    }
}
