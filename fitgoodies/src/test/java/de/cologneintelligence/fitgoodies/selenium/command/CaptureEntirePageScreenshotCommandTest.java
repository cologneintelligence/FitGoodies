package de.cologneintelligence.fitgoodies.selenium.command;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class CaptureEntirePageScreenshotCommandTest extends FitGoodiesTestCase {

	private CommandProcessor commandProcessor;
	private WrappedCommand command;
	
	protected void setUp() throws Exception {
		super.setUp();
		commandProcessor = mock(CommandProcessor.class);
		SetupHelper.instance().setCommandProcessor(commandProcessor);
		SetupHelper.instance().setSleepBeforeScreenshot(1l);
	}

	public void testDoCommand() {
		
		final String[] args = new String[]{"arg1", "arg2"};
		command = CommandFactory.createCommand("captureEntirePageScreenshot", args);
	    checking(new Expectations() {{
	    	oneOf(commandProcessor).doCommand("captureEntirePageScreenshot", args);
			will(returnValue("OK"));
		}});
	    
	    assertEquals("OK", command.execute());
	}

}
