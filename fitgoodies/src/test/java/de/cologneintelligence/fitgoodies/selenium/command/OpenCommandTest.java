package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.WrappedCommand;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class OpenCommandTest extends FitGoodiesTestCase {

    private CommandProcessor commandProcessor;
    private WrappedCommand openCommand;
    private SetupHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RunnerHelper runnerHelper = DependencyManager.getOrCreate(RunnerHelper.class);
        helper = DependencyManager.getOrCreate(SetupHelper.class);

        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        runnerHelper.setResultFilePath("fixture.html");
    }

    public void testDoCommand() {
        final String[] args = new String[]{"arg1", "arg2"};
        openCommand = CommandFactory.createCommand("openSomething", args, helper);

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("openSomething", args);
            will(throwException(new SeleniumException("Error")));
        }});

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("waitForPageToLoad", new String[] { "50000", });
            will(returnValue("OK"));
        }});

        assertEquals("OK", openCommand.execute());
    }
}
