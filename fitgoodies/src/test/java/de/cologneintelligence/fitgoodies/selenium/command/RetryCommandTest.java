package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.WrappedCommand;

public class RetryCommandTest extends FitGoodiesTestCase {

    private CommandProcessor commandProcessor;
    private WrappedCommand retryCommand;
    final String[] args = new String[]{"arg1", "arg2"};
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        commandProcessor = mock(CommandProcessor.class);
        SetupHelper.instance().setCommandProcessor(commandProcessor);
        SetupHelper.instance().setTakeScreenshots(false);
        SetupHelper.instance().setSleepBeforeScreenshot(1L);
        RunnerHelper.instance().setResultFilePath("fixture.html");
        retryCommand = CommandFactory.createCommand("commandAndRetry",args);

    }

    public void testDoCommand4Times() {

        SetupHelper.instance().setTimeout("200");
        SetupHelper.instance().setInterval("50");

        checking(new Expectations() {{
            exactly(4).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});
        try {
            retryCommand.execute();
        } catch (RetryException e) {
            assertEquals("TimeoutError!; attempts: 4/4 times", e.getMessage());
        }
    }

    public void testDoCommand6Times() {

        final String[] args = new String[]{"arg1", "arg2"};
        SetupHelper.instance().setTimeout("600");
        SetupHelper.instance().setInterval("100");

        checking(new Expectations() {{
            exactly(6).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});

        try {
            retryCommand.execute();
        } catch (RetryException e) {
            assertEquals("TimeoutError!; attempts: 6/6 times", e.getMessage());
        }
    }

    public void testDoCommandFirst5ReturnsNOKThenOK() {

        final String[] args = new String[]{"arg1", "arg2"};
        SetupHelper.instance().setTimeout("1600");
        SetupHelper.instance().setInterval("100");

        checking(new Expectations() {{
            exactly(3).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK; attempts: 4 times", retryCommand.execute());
    }
}
