package de.cologneintelligence.fitgoodies.selenium;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.DateProvider;
import de.cologneintelligence.fitgoodies.references.processors.DateProviderCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.selenium.SeleniumFixture;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

import fit.Parse;

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
			oneOf(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(returnValue("NOK"));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
	}

	public void testInvokeSeleniumCommandThrowsSeleniumException() throws Exception {
		checking(new Expectations() {{
			atLeast(1).of(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(throwException(new SeleniumException("Error")));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
	}
	
	public void testInvokeSeleniumCommandThrowsException() throws Exception {
		assertEquals(0, fixture.counts.exceptions);
		checking(new Expectations() {{
			oneOf(commandProcessor).doCommand("command", new String[]{"arg1", "arg2"});
			will(throwException(new RuntimeException("Error")));
		}});
		fixture.doTable(table);
		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(1, fixture.counts.exceptions);
	}

    public void testInvokeSeleniumWithCrossReference() throws Exception {
        Parse table = new Parse(
                "<table>"
                + "<tr><td>ignore</td></tr>"
                + "<tr><td>command</td><td>arg1</td><td>${dateProvider.getCurrentDate()}</td></tr>"
                + "</table>");
        final DateProvider dateProvider = mock(DateProvider.class);
        final String date = "21.01.2009";
        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);
        CrossReferenceHelper.instance().getProcessors().remove(processor);
        CrossReferenceHelper.instance().getProcessors().add(processor);
        checking(new Expectations() {{
            oneOf(dateProvider).getCurrentDate();
            will(returnValue(date));
        }});

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", new String[]{"arg1", date});
            will(returnValue("OK"));
        }});
        fixture.doTable(table);
        assertEquals(1, fixture.counts.right);  
    }
}
