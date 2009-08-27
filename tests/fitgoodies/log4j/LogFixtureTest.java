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
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.references.CrossReferenceHelper;

/**
 * @author jwierum
 * @version $Id$
 *
 */
public final class LogFixtureTest extends FitGoodiesTestCase {
	public LogFixtureTest() {
		setImposteriser(ClassImposteriser.INSTANCE);
	}

	private LoggerProvider logs;
	private Logger rootLogger;
	private Logger logger;
	private CaptureAppender appender;
	private CaptureAppender rootAppender;
	private CellArgumentParserFactory cellArgumentParserFactory;
	private LogEventAnalyzerFactory logEventAnalyzerFactory;
	private CellArgumentParser cellArgumentParser;
	private LogEventAnalyzer logEventAnalyzer;
	private Map<String, String> parameterMap;

	private static class BaseAppender extends AppenderSkeleton {
		public BaseAppender(final String name) { setName(name); }
		@Override protected void append(final LoggingEvent arg0) { }
		@Override public void close() { }
		@Override public boolean requiresLayout() { return false; }
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		logs = mock(LoggerProvider.class);
		logger = mock(Logger.class, "logger");
		rootLogger = mock(Logger.class, "rootLogger");

		cellArgumentParserFactory = mock(CellArgumentParserFactory.class);
		logEventAnalyzerFactory = mock(LogEventAnalyzerFactory.class);
		cellArgumentParser = mock(CellArgumentParser.class);
		logEventAnalyzer = mock(LogEventAnalyzer.class);

		appender = CaptureAppender.newAppenderFrom(new BaseAppender("ap1"));
		rootAppender = CaptureAppender.newAppenderFrom(new BaseAppender("rap1"));
		parameterMap = new HashMap<String, String>();
	}

	private void prepareFactories(final int numberOfCallsToRootLogger,
			final int numberOfCallsToClassLogger) {
		checking(new Expectations() {{
			exactly(numberOfCallsToClassLogger).of(logs).getLogger("com.myproject.class1");
				will(returnValue(logger));
			exactly(numberOfCallsToRootLogger).of(logs).getRootLogger();
				will(returnValue(rootLogger));

			atLeast(numberOfCallsToRootLogger).of(rootLogger).getAppender(
					CaptureAppender.getAppenderNameFor("R"));
				will(returnValue(rootAppender));
			atLeast(numberOfCallsToClassLogger).of(logger).getAppender(
					CaptureAppender.getAppenderNameFor("stdout"));
				will(returnValue(appender));

			exactly(numberOfCallsToRootLogger + numberOfCallsToClassLogger).of(
					cellArgumentParserFactory).getParserFor(with(any(Parse.class)));
				will(returnValue(cellArgumentParser));

			exactly(numberOfCallsToRootLogger + numberOfCallsToClassLogger).of(
					cellArgumentParser).getExtractedCommandParameters();
				will(returnValue(parameterMap));

			exactly(numberOfCallsToRootLogger + numberOfCallsToClassLogger).of(
					logEventAnalyzerFactory).getLogEventAnalyzerFor(
					with(any(LogFixture.class)), with(any(Parse.class)),
					with(any(LoggingEvent[].class)));
				will(returnValue(logEventAnalyzer));
		}});
	}

	public void testParseContains() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>com.myproject.class1</td><td>stdout</td>"
						+ "<td>contains</td><td>a message</td></tr>"
				+ "<tr><td>com.myproject.class1</td><td>stdout</td>"
						+ "<td>notContains</td><td>a message</td></tr>"
				+ "<tr><td>rootLogger</td><td>R</td>"
						+ "<td>containsException</td><td>rOOt</td></tr>"
				+ "<tr><td>rootLogger</td><td>R</td>"
						+ "<td>contains</td><td>rOOt</td></tr>"
				+ "<tr><td>rootLogger</td><td>R</td>"
						+ "<td>notContainsException</td><td>rOOt</td></tr>"
				+ "</table>");

		final int NO_OF_CLASS_CALLS = 2;
		final int NO_OF_ROOT_CALLS = 3;
		prepareFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);

		final Sequence sequence = sequence("analyzer");
		checking(new Expectations() {{
			oneOf(logEventAnalyzer).processContains(parameterMap);
				inSequence(sequence);
			oneOf(logEventAnalyzer).processNotContains(parameterMap);
				inSequence(sequence);
			oneOf(logEventAnalyzer).processContainsException(parameterMap);
				inSequence(sequence);
			oneOf(logEventAnalyzer).processContains(parameterMap);
				inSequence(sequence);
			oneOf(logEventAnalyzer).processNotContainsException(parameterMap);
				inSequence(sequence);
		}});

		LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
				logEventAnalyzerFactory);

		fixture.doTable(table);
	}

	public void testIllegalCommand() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>rootLogger</td><td>R</td>"
						+ "<td>what is this?</td><td>rOOt</td></tr>"
				+ "</table>");

		final int NO_OF_CLASS_CALLS = 0;
		final int NO_OF_ROOT_CALLS = 1;
		prepareFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);

		LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
				logEventAnalyzerFactory);

		fixture.doTable(table);
		assertEquals(1, fixture.counts.exceptions);
		assertContains("unknown command", table.parts.more.parts.more.more.text());
	}

	public void testIllegalParameters() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>com.myproject.class1</td><td>stdout</td>"
						+ "<td>notcontainsException[nonsense]</td><td>xxx</td></tr>"
				+ "</table>");

		LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
				logEventAnalyzerFactory);

		checking(new Expectations() {{
			oneOf(logs).getLogger("com.myproject.class1");
				will(returnValue(logger));
			oneOf(logger).getAppender(
					CaptureAppender.getAppenderNameFor("stdout"));
				will(returnValue(appender));

			oneOf(cellArgumentParserFactory).getParserFor(with(any(Parse.class)));
				will(throwException(new IllegalArgumentException("")));
		}});

		fixture.doTable(table);

		assertEquals(1, fixture.counts.exceptions);
		assertContains("Illegal format", table.parts.more.parts.more.more.text());
	}

	@SuppressWarnings("static-access")
	public void testIllegalLogger() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>nonsense</td><td>stdout</td>"
						+ "<td>contains</td><td>error</td></tr>"
				+ "<tr><td>rootLogger</td><td>nonsense</td>"
						+ "<td>contains</td><td>error</td></tr>"
				+ "</table>");

		checking(new Expectations() {{
			oneOf(logs).getLogger("nonsense"); will(returnValue(null));
			oneOf(logs).getRootLogger(); will(returnValue(rootLogger));
			oneOf(rootLogger).getLogger("nonsense-fitgoodiescapture");
				will(returnValue(null));
		}});

		LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
				logEventAnalyzerFactory);

		fixture.doTable(table);

		assertEquals(2, fixture.counts.exceptions);
		assertContains("Invalid logger", table.parts.more.parts.text());
		assertContains("Invalid appender", table.parts.more.more.parts.more.text());
	}

	public void testCrossReferences() throws Exception {
		CrossReferenceHelper.instance().parseBody("${a.put(message)}", "a message");
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>com.myproject.class1</td><td>stdout</td>"
						+ "<td>contains</td><td>${a.get(message)}</td></tr>"
				+ "</table>");

		final int NO_OF_CLASS_CALLS = 1;
		final int NO_OF_ROOT_CALLS = 0;
		prepareFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);

		checking(new Expectations() {{
			oneOf(logEventAnalyzer).processContains(parameterMap);
		}});

		LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
				logEventAnalyzerFactory);

		fixture.doTable(table);
		assertEquals("a message", table.parts.more.parts.more.more.more.text());
	}
}
