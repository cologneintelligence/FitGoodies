/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public final class LogFixtureTest extends FitGoodiesFixtureTestCase<LogFixture> {
	private static final String NAMESPACE = "com.myproject.class1";

	@Mock
	private LoggerProvider logs;

	@Mock(name = "rootLogger")
	private Logger rootLogger;

	@Mock(name = "logger")
	private Logger logger;

	@Mock
	private LogEventAnalyzerFactory logEventAnalyzerFactory;

	@Mock
	private LogEventAnalyzer logEventAnalyzer;

	private Map<String, String> parameterMap;

	private CaptureAppender appender;
	private CaptureAppender rootAppender;

	@Override
	protected Class<LogFixture> getFixtureClass() {
		return LogFixture.class;
	}

	@Override
	protected LogFixture newInstance() throws InstantiationException, IllegalAccessException {
		return new LogFixture(logs, logEventAnalyzerFactory);
	}

	@Before
	public void setUp() throws Exception {
		appender = CaptureAppender.newAppenderFrom(new DummyAppender("ap1"));
		rootAppender = CaptureAppender.newAppenderFrom(new DummyAppender("rap1"));
		parameterMap = new HashMap<>();
	}

	private void prepareFactories() {
		when(logs.getLogger(NAMESPACE)).thenReturn(logger);
		when(logs.getRootLogger()).thenReturn(rootLogger);
		when(rootLogger.getAppender(CaptureAppender.getAppenderNameFor("R")))
				.thenReturn(rootAppender);
		when(logger.getAppender(CaptureAppender.getAppenderNameFor("stdout"))).thenReturn(appender);

		when(logEventAnalyzerFactory.getLogEventAnalyzerFor(
            argThatSame(validator),
				any(FitCell.class),
				any(LoggingEvent[].class)))
				.thenReturn(logEventAnalyzer);
	}


	private void verifyFactories(int numberOfCallsToRootLogger, int numberOfCallsToClassLogger) {
		verify(logs, times(numberOfCallsToClassLogger)).getLogger(NAMESPACE);
		verify(logger, atLeast(numberOfCallsToClassLogger))
				.getAppender(CaptureAppender.getAppenderNameFor("stdout"));

		verify(logs, times(numberOfCallsToRootLogger)).getRootLogger();
		verify(rootLogger, atLeast(numberOfCallsToRootLogger))
				.getAppender(CaptureAppender.getAppenderNameFor("R"));

		verify(logEventAnalyzerFactory, times(numberOfCallsToRootLogger + numberOfCallsToClassLogger))
				.getLogEventAnalyzerFor(argThatSame(validator),
						any(FitCell.class), any(LoggingEvent[].class));

        verifyNoMoreInteractions(logs, logger, rootLogger, logEventAnalyzer);
	}

	@Test
	public void testParseContains() {
		useTable(
				tr("com.myproject.class1", "stdout", "contains", "a message"),
				tr("com.myproject.class1", "stdout", "notContains", "a message"),
				tr("rootLogger", "R", "containsException", "rOOt"),
				tr("rootLogger", "R", "contains", "rOOt"),
				tr("rootLogger", "R", "notContainsException", "rOOt"));

		final int NO_OF_CLASS_CALLS = 2;
		final int NO_OF_ROOT_CALLS = 3;
		prepareFactories();

		run();

		InOrder order = inOrder(logEventAnalyzer);
		order.verify(logEventAnalyzer).processContains(parameterMap);
		order.verify(logEventAnalyzer).processNotContains(parameterMap);
		order.verify(logEventAnalyzer).processContainsException(parameterMap);
		order.verify(logEventAnalyzer).processContains(parameterMap);
		order.verify(logEventAnalyzer).processNotContainsException(parameterMap);

		verifyFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);
	}

	@Test
	public void testIllegalCommand() {
		useTable(tr("rootLogger", "R", "what is this?", "rOOt"));

		final int NO_OF_CLASS_CALLS = 0;
		final int NO_OF_ROOT_CALLS = 1;
		prepareFactories();

		run();
        assertCounts(0, 0, 0, 1);
		assertThat(htmlAt(0, 2), containsString("unknown command"));

		verifyFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);
	}


	@Test
	public void testIllegalLogger() {
		useTable(
				tr("nonsense", "stdout", "contains", "error"),
				tr("rootLogger", "nonsense", "contains", "error"));

		when(logs.getRootLogger()).thenReturn(rootLogger);

		run();

        assertCounts(0, 0, 0, 2);
		assertThat(htmlAt(0, 0), containsString("Invalid logger"));
		assertThat(htmlAt(1, 1), containsString("Invalid appender"));
	}
}
