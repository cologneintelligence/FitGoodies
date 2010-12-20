package fitgoodies.selenium;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

public class SeleniumFixtureTest extends FitGoodiesTestCase {

	private CommandProcessor commandProcessor;
	private SeleniumFixture fixture;
	private Parse table;
	public void setUp() throws Exception {
		commandProcessor = mock(CommandProcessor.class);
		SetupHelper.instance().setCommandProcessor(commandProcessor);
		fixture = new SeleniumFixture();

		table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>command</td><td>arg1</td><td>arg2</td></tr>"
				+ "</table>");
	}

	public void testInvokeSeleniumCommandReturnsOK() throws Exception {				
		checking(new Expectations() {{
			oneOf(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(returnValue("OK"));
		}});
		fixture.doTable(table);
		assertEquals(1, fixture.counts.right);	
	}
	
	public void testInvokeSeleniumCommandReturnsNOK() throws Exception {
		checking(new Expectations() {{
			atLeast(1).of(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(returnValue("NOK"));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
	}

	public void testInvokeSeleniumCommandThrowsSeleniumException() throws Exception {
		checking(new Expectations() {{
			oneOf(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(throwException(new SeleniumException("Error")));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
	}
	
	public void testInvokeSeleniumCommandThrowsException() throws Exception {
		checking(new Expectations() {{
			oneOf(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(throwException(new RuntimeException("Error")));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(1, fixture.counts.exceptions);
	}
}
