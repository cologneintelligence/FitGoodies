package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.selenium.Retry;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;



public class RetryCommand extends WrappedCommand {

    public RetryCommand(final String command, final String[] args) {
        super(command, args);
    }

    @Override
    public String execute() {
        Retry retry = new Retry(SetupHelper.instance().getTimeout(), SetupHelper.instance().getInterval()) {
            private String result;
            @Override
            public boolean execute() {
                try{
                    String seleniumCommand = command.substring(0, command.indexOf("AndRetry"));
                    result = commandProcessor.doCommand(seleniumCommand, args);
                } catch (SeleniumException e) {
                    throw new RetryException(e.getMessage() + "; " + attemptMessage(attemptCounter()));
                }
                return result.startsWith("OK");
            }
        };
        return (retry.start() ? "OK; " : "NOK; ") + attemptMessage(retry.attemptCounter());
    }

    private String attemptMessage(final int count) {
        long maxCount = SetupHelper.instance().getTimeout() / SetupHelper.instance().getInterval();
        return "attempts: " + count + "/" + maxCount + " times";
    }


}
