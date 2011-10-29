package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.command.CaptureEntirePageScreenshotCommand;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.OpenCommand;
import de.cologneintelligence.fitgoodies.selenium.command.RetryCommand;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumCommand;

public class CommandFactoryTest extends FitGoodiesTestCase {
	String[] args = new String[]{};
	
	
	public void testCommandAndRetry() {		
		assertEquals(RetryCommand.class, CommandFactory.createCommand("commandAndRetry", args ).getClass());
	}

	public void testCommandOpen() {		
		assertEquals(OpenCommand.class, CommandFactory.createCommand("open", args).getClass());
	}

	public void testCommandCaptureEntirePageScreenshot() {		
		assertEquals(CaptureEntirePageScreenshotCommand.class, CommandFactory.createCommand("captureEntirePageScreenshot", args).getClass());
	}

	public void testCommand() {		
		assertEquals(SeleniumCommand.class, CommandFactory.createCommand("command", args).getClass());
	}
}
