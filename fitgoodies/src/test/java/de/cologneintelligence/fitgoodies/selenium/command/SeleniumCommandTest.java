package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumCommand;

public class SeleniumCommandTest extends FitGoodiesTestCase {

    public void testDoCommand() {
        final CommandProcessor commandProcessor = mock(CommandProcessor.class);

        final String[] args = new String[]{"arg1", "arg2"};
        final SeleniumCommand wrappedCommand = new SeleniumCommand("command", args, commandProcessor);

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK", wrappedCommand.execute());
    }

}
