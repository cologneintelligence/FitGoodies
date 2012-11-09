package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class CaptureEntirePageScreenshotCommand extends WrappedCommand {
    private final long sleepBeforeScreenshot;

    public CaptureEntirePageScreenshotCommand(final String command, final String[] args,
            final SetupHelper helper) {
        super(command, args, helper.getCommandProcessor());
        this.sleepBeforeScreenshot = helper.getSleepBeforeScreenshotMillis();
    }

    @Override
    public String execute() {
        waitBeforeTakingScreenshot();
        return commandProcessor.doCommand("captureEntirePageScreenshot", args);
    }

    private void waitBeforeTakingScreenshot() {
        try {
            Thread.sleep(sleepBeforeScreenshot);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
