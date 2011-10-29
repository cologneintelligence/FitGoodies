package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class CaptureEntirePageScreenshotCommand extends WrappedCommand {
	private final Long sleepBeforeScreenshot = SetupHelper.instance().getSleepBeforeScreenshot();

	public CaptureEntirePageScreenshotCommand(String command, String[] args) {
		super(command, args);
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
