package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class RetryCommandTest extends FitGoodiesTestCase {
    private SetupHelper helper;
    private CommandProcessor commandProcessor;
    private WrappedCommand retryCommand;

    private final String[] args = new String[]{"arg1", "arg2"};

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final RunnerHelper runnerHelper = DependencyManager.getOrCreate(
                RunnerHelper.class);

        helper = DependencyManager.getOrCreate(SetupHelper.class);
        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        helper.setTakeScreenshots(false);
        helper.setSleepBeforeScreenshotMillis(1L);
        runnerHelper.setResultFilePath("fixture.html");
        retryCommand = CommandFactory.createCommand("commandAndRetry", args, helper);

    }

    public void testDoCommand4Times() {
        helper.setTimeout(200);
        helper.setInterval(50);

        checking(new Expectations() {{
            exactly(4).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});
        try {
            retryCommand.execute();
        } catch (final RetryException e) {
            assertEquals("TimeoutError!; attempts: 4/4 times", e.getMessage());
        }
    }

    public void testDoCommand6Times() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setTimeout(600);
        helper.setInterval(100);

        checking(new Expectations() {{
            exactly(6).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});

        try {
            retryCommand.execute();
        } catch (final RetryException e) {
            assertEquals("TimeoutError!; attempts: 6/6 times", e.getMessage());
        }
    }

    public void testDoCommandFirst5ReturnsNOKThenOK() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setTimeout(1600);
        helper.setInterval(100);

        checking(new Expectations() {{
            exactly(3).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK; attempts: 4 times", retryCommand.execute());
    }
}
