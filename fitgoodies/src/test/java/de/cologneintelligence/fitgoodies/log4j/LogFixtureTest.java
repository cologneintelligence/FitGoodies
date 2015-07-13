/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Fixture;
import fit.Parse;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public final class LogFixtureTest extends FitGoodiesTestCase {
    private static final String NAMESPACE = "com.myproject.class1";

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

    @Before
    public void setUp() throws Exception {
        logs = mock(LoggerProvider.class);
        logger = mock(Logger.class, "logger");
        rootLogger = mock(Logger.class, "rootLogger");

        cellArgumentParserFactory = mock(CellArgumentParserFactory.class);
        logEventAnalyzerFactory = mock(LogEventAnalyzerFactory.class);
        cellArgumentParser = mock(CellArgumentParser.class);
        logEventAnalyzer = mock(LogEventAnalyzer.class);

        appender = CaptureAppender.newAppenderFrom(new BaseAppender("ap1"));
        rootAppender = CaptureAppender.newAppenderFrom(new BaseAppender("rap1"));
        parameterMap = new HashMap<>();
    }

    private void prepareFactories() {
        when(logs.getLogger(NAMESPACE)).thenReturn(logger);
        when(logs.getRootLogger()).thenReturn(rootLogger);
        when(rootLogger.getAppender(CaptureAppender.getAppenderNameFor("R")))
                .thenReturn(rootAppender);
        when(logger.getAppender(CaptureAppender.getAppenderNameFor("stdout"))).thenReturn(appender);

        when(cellArgumentParserFactory.getParserFor(argThat(any(Parse.class))))
                .thenReturn(cellArgumentParser);

        when(cellArgumentParser.getExtractedCommandParameters())
                .thenReturn(parameterMap);

        when(logEventAnalyzerFactory.getLogEventAnalyzerFor(
                argThat(any(LogFixture.class)), argThat(any(Parse.class)),
                argThat(any(LoggingEvent[].class))))
                .thenReturn(logEventAnalyzer);
    }


    private void verifyFactories(final int numberOfCallsToRootLogger,
                                  final int numberOfCallsToClassLogger) {

        verify(logs, times(numberOfCallsToClassLogger)).getLogger(NAMESPACE);
        verify(logger, atLeast(numberOfCallsToClassLogger))
                    .getAppender(CaptureAppender.getAppenderNameFor("stdout"));

        verify(logs, times(numberOfCallsToRootLogger)).getRootLogger();
        verify(rootLogger, atLeast(numberOfCallsToRootLogger))
                .getAppender(CaptureAppender.getAppenderNameFor("R"));

        verify(cellArgumentParserFactory, times(numberOfCallsToRootLogger + numberOfCallsToClassLogger))
                .getParserFor(argThat(any(Parse.class)));
        verify(cellArgumentParser, times(numberOfCallsToRootLogger + numberOfCallsToClassLogger))
                .getExtractedCommandParameters();
        verify(logEventAnalyzerFactory, times(numberOfCallsToRootLogger + numberOfCallsToClassLogger))
                .getLogEventAnalyzerFor(argThat(any(Fixture.class)), argThat(any(Parse.class)),
                        argThat(any(LoggingEvent[].class)));
    }

    @Test
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
        prepareFactories();


        LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
                logEventAnalyzerFactory);

        fixture.doTable(table);

        InOrder order = inOrder(logEventAnalyzer);
        order.verify(logEventAnalyzer).processContains(parameterMap);
        order.verify(logEventAnalyzer).processNotContains(parameterMap);
        order.verify(logEventAnalyzer).processContainsException(parameterMap);
        order.verify(logEventAnalyzer).processContains(parameterMap);
        order.verify(logEventAnalyzer).processNotContainsException(parameterMap);

        verifyFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);
    }

    @Test
    public void testIllegalCommand() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>rootLogger</td><td>R</td>"
                + "<td>what is this?</td><td>rOOt</td></tr>"
                + "</table>");

        final int NO_OF_CLASS_CALLS = 0;
        final int NO_OF_ROOT_CALLS = 1;
        prepareFactories();

        LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
                logEventAnalyzerFactory);

        fixture.doTable(table);
        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
        assertThat(table.parts.more.parts.more.more.text(), containsString("unknown command"));

        verifyFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);
    }

    @Test
    public void testIllegalParameters() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>com.myproject.class1</td><td>stdout</td>"
                + "<td>notcontainsException[nonsense]</td><td>xxx</td></tr>"
                + "</table>");

        LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
                logEventAnalyzerFactory);

        when(logs.getLogger("com.myproject.class1")).thenReturn(logger);
        when(logger.getAppender(CaptureAppender.getAppenderNameFor("stdout")))
            .thenReturn(appender);
        when(cellArgumentParserFactory.getParserFor(argThat(any(Parse.class))))
            .thenThrow(new IllegalArgumentException(""));

        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
        assertThat(table.parts.more.parts.more.more.text(), containsString("Illegal format"));
    }

    @Test
    @SuppressWarnings("static-access")
    public void testIllegalLogger() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>nonsense</td><td>stdout</td>"
                + "<td>contains</td><td>error</td></tr>"
                + "<tr><td>rootLogger</td><td>nonsense</td>"
                + "<td>contains</td><td>error</td></tr>"
                + "</table>");

        when(logs.getRootLogger()).thenReturn(rootLogger);

        LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
                logEventAnalyzerFactory);

        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 2)));
        assertThat(table.parts.more.parts.text(), containsString("Invalid logger"));
        assertThat(table.parts.more.more.parts.more.text(), containsString("Invalid appender"));
    }

    @Test
    public void testCrossReferences() throws Exception {
        CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        helper.parseBody("${a.put(message)}", "a message");
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>com.myproject.class1</td><td>stdout</td>"
                + "<td>contains</td><td>${a.get(message)}</td></tr>"
                + "</table>");

        final int NO_OF_CLASS_CALLS = 1;
        final int NO_OF_ROOT_CALLS = 0;
        prepareFactories();

        LogFixture fixture = new LogFixture(logs, cellArgumentParserFactory,
                logEventAnalyzerFactory);

        fixture.doTable(table);
        assertThat(table.parts.more.parts.more.more.more.text(), is(equalTo("a message")));

        verifyFactories(NO_OF_ROOT_CALLS, NO_OF_CLASS_CALLS);
        verify(logEventAnalyzer).processContains(parameterMap);
    }
}
