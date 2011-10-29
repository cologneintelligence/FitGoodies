package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.WrappedCommand;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumCommand;

public class SeleniumCommandTest extends FitGoodiesTestCase {

	private CommandProcessor commandProcessor;
	private WrappedCommand wrappedCommand;
	
	protected void setUp() throws Exception {
		super.setUp();
		commandProcessor = mock(CommandProcessor.class);
		SetupHelper.instance().setCommandProcessor(commandProcessor);
	}

	public void testDoCommand() {
		
		final String[] args = new String[]{"arg1", "arg2"};
		wrappedCommand = new SeleniumCommand("command", args);
	    checking(new Expectations() {{
	    	oneOf(commandProcessor).doCommand("command", args);
			will(returnValue("OK"));
		}});
	    
	    assertEquals("OK", wrappedCommand.execute());
	}

}
