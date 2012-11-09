package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CaptureEntirePageScreenshotCommand;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.OpenCommand;
import de.cologneintelligence.fitgoodies.selenium.command.RetryCommand;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumCommand;

public class CommandFactoryTest extends FitGoodiesTestCase {
    private final String[] args = new String[]{};

    public void testCommandAndRetry() {
        assertEquals(RetryCommand.class,
                CommandFactory.createCommand("commandAndRetry", args, new SetupHelper()).getClass());
        assertEquals(RetryCommand.class,
                CommandFactory.createCommand("blaAndRetry", args, new SetupHelper()).getClass());
    }

    public void testCommandOpen() {
        assertEquals(OpenCommand.class,
                CommandFactory.createCommand("open", args, new SetupHelper()).getClass());
    }

    public void testCommandCaptureEntirePageScreenshot() {
        assertEquals(CaptureEntirePageScreenshotCommand.class,
                CommandFactory.createCommand("captureEntirePageScreenshot", args,
                        new SetupHelper()).getClass());
    }

    public void testCommand() {
        assertEquals(SeleniumCommand.class, CommandFactory.createCommand("command", args, new SetupHelper()).getClass());
        assertEquals(SeleniumCommand.class, CommandFactory.createCommand("bla", args, new SetupHelper()).getClass());
    }
}
