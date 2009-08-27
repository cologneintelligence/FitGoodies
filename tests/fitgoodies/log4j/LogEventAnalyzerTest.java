/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package fitgoodies.log4j;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;

import fit.Fixture;
import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * @author jwierum
 * @version $Id: LogEventAnalyzerTest.java 196 2009-08-21 09:58:46Z jwierum $
 *
 */
public final class LogEventAnalyzerTest extends FitGoodiesTestCase {
	public LogEventAnalyzerTest() {
		setImposteriser(ClassImposteriser.INSTANCE);
	}

	private LoggingEvent[] list;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		list = prepareCheckForGreenTest();
	}

	private LoggingEvent[] prepareCheckForGreenTest() {
		List<LoggingEvent> list = new LinkedList<LoggingEvent>();

		list.add(new LoggingEvent("com.fqdn.class1", null,
				100, Level.ERROR, "a message", "thread1",
				new ThrowableInformation(new RuntimeException("xxx")),
				"ndc", null, null));
		list.add(new LoggingEvent("com.fqdn.class1", null, 120,
				Level.INFO, "no error", "thread2", null, "ndc", null, null));
		list.add(new LoggingEvent("rootLogger", null, 140, Level.DEBUG,
				"a root message", "main",
				new ThrowableInformation(new RuntimeException("yyy")),
				null, null, null));

		return list.toArray(new LoggingEvent[]{});
	}

	private Parse makeCell(final String string) throws ParseException {
		return new Parse("<td>" + string + "</td>", new String[]{"td"});
	}

	public void testParseContains() throws ParseException {
		final Fixture fixture = mock(Fixture.class);
		final Parse cell1 = makeCell("a message");
		final Parse cell2 = makeCell("rOOt");
		final Parse cell3 = makeCell("non existing message");

		checking(new Expectations() {{
			oneOf(fixture).right(cell1);
			oneOf(fixture).right(cell2);
			oneOf(fixture).wrong(cell3);
			oneOf(fixture).info(cell1, "(expected)");
			oneOf(fixture).info(cell1, "(actual)");
			oneOf(fixture).info(cell2, "(expected)");
			oneOf(fixture).info(cell2, "(actual)");
		}});

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processContains(new HashMap<String, String>());

		assertEquals("a messagea message", cell1.text());
		assertEquals("rOOta root message", cell2.text());
		assertEquals("non existing message", cell3.text());
	}

	public void testParseWithParameters() throws ParseException {
		final Fixture fixture = mock(Fixture.class);
		final Parse cell1 = makeCell("no error");
		final Parse cell2 = makeCell("root");
		final Parse cell3 = makeCell("no error");
		final Parse cell4 = makeCell("no error");

		checking(new Expectations() {{
			oneOf(fixture).right(cell1);
			oneOf(fixture).right(cell2);
			oneOf(fixture).right(cell3);
			oneOf(fixture).wrong(cell4);
			oneOf(fixture).info(cell1, "(expected)");
			oneOf(fixture).info(cell1, "(actual)");
		}});

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("minlevel", "Info");
		parameters.put("thread", "thread2");
		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(fixture, cell1, list);
		analyzer.processContains(parameters);

		parameters.clear();
		parameters.put("minlevel", "error");
		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "main");
		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "thread5");
		analyzer = new LogEventAnalyzerImpl(fixture, cell4, list);
		analyzer.processContains(parameters);

		assertEquals("no errorno error", cell1.text());
		assertEquals("root", cell2.text());
		assertEquals("no error", cell3.text());
		assertEquals("no error", cell4.text());
	}

	public void testNotContains() throws ParseException {
		final Fixture fixture = mock(Fixture.class);
		final Parse cell1 = makeCell("an error");
		final Parse cell2 = makeCell("toor");
		final Parse cell3 = makeCell("root");

		checking(new Expectations() {{
			oneOf(fixture).right(cell1);
			oneOf(fixture).right(cell2);
			oneOf(fixture).wrong(cell3);
			oneOf(fixture).info(cell3, "(expected)");
			oneOf(fixture).info(cell3, "(actual)");
		}});

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContains(new HashMap<String, String>());

		assertEquals("an error", cell1.text());
		assertEquals("toor", cell2.text());
		assertEquals("roota root message", cell3.text());
	}

	public void testContainsException() throws ParseException {
		final Fixture fixture = mock(Fixture.class);
		final Parse cell1 = makeCell("xXx");
		final Parse cell2 = makeCell("RuntiMEException");
		final Parse cell3 = makeCell("IllegalStateException");

		checking(new Expectations() {{
			oneOf(fixture).right(cell1);
			oneOf(fixture).right(cell2);
			oneOf(fixture).wrong(cell3);
			oneOf(fixture).info(cell1, "(expected)");
			oneOf(fixture).info(cell1, "(actual)");
			oneOf(fixture).info(cell2, "(expected)");
			oneOf(fixture).info(cell2, "(actual)");
		}});

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processContainsException(new HashMap<String, String>());

		assertEquals("xXxjava.lang.RuntimeException: xxx", cell1.text());
		assertEquals("RuntiMEExceptionjava.lang.RuntimeException: xxx", cell2.text());
		assertEquals("IllegalStateException", cell3.text());
	}

	public void testNotContainsException() throws ParseException {
		final Fixture fixture = mock(Fixture.class);
		final Parse cell1 = makeCell("Error message");
		final Parse cell2 = makeCell("IllegalStateException");
		final Parse cell3 = makeCell("Exception");

		checking(new Expectations() {{
			oneOf(fixture).right(cell1);
			oneOf(fixture).right(cell2);
			oneOf(fixture).wrong(cell3);
			oneOf(fixture).info(cell3, "(expected)");
			oneOf(fixture).info(cell3, "(actual)");
		}});

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		assertEquals("Error message", cell1.text());
		assertEquals("IllegalStateException", cell2.text());
		assertEquals("Exceptionjava.lang.RuntimeException: xxx", cell3.text());
	}
}
