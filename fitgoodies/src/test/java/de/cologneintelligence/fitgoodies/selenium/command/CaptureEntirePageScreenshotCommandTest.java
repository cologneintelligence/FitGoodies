package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class CaptureEntirePageScreenshotCommandTest extends FitGoodiesTestCase {
    private CommandProcessor commandProcessor;
    private WrappedCommand command;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        commandProcessor = mock(CommandProcessor.class);
    }

    public void testDoCommand() {
        SetupHelper helper = new SetupHelper();

        helper.setCommandProcessor(commandProcessor);
        helper.setSleepBeforeScreenshotMillis(1L);

        final String[] args = new String[]{"arg1", "arg2"};
        command = CommandFactory.createCommand("captureEntirePageScreenshot", args, helper);
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("captureEntirePageScreenshot", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK", command.execute());
    }

}
