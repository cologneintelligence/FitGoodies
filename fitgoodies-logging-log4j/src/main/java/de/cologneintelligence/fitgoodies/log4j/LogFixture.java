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

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;

import java.util.List;
import java.util.Map;

/**
 * Fixture to analyze captured log data. Log messages can be analyzed by searing
 * strings in their messages and exception information.<br>
 * The fixture contains 4 rows: the name of the logger, the name of the appender,
 * a command which can have parameters, and a expression to search. The parameter
 * column supports Cross References.
 * Only captured loggers can be analyzed. To capture loggers, see {@link SetupFixture}.
 * <p>
 * The root logger is named &quot;rootLogger&quot;.
 * Valid parameters are &quot;Thread&quot; and &quot;MinLevel&quot;.
 * <p>
 * Example:
 * <table border="1" summary=""><tr><td>fitgoodies.log4j.LogFixture</td></tr>
 * <tr><td>rootLogger</td><td>R</td><td>contains</td><td>successfully initialized</td></tr>
 * <tr><td>rootLogger</td><td>R</td><td>notContains</td><td>critical error</td></tr>
 * <tr>
 * <td>org.example.MyClass</td>
 * <td>stdout</td>
 * <td>containsException</td>
 * <td>IllegalArgumentException</td>
 * </tr>
 * <tr>
 * <td>org.example.MyClass</td>
 * <td>stdout</td>
 * <td>notContainsException</td>
 * <td>not found</td>
 * </tr>
 * <tr>
 * <td>org.example.MyClass</td>
 * <td>stdout</td>
 * <td>contains[Thread = main, MinLevel = Error]</td>
 * <td>timeout</td>
 * </tr>
 * </table>
 */
public class LogFixture extends Fixture {
	private final LoggerProvider loggerProvider;
	private final LogEventAnalyzerFactory logEventAnalyzerFactory;

	private static final int LOGGER_COLUMN = 0;
	private static final int APPENDER_COLUMN = 1;
	private static final int COMMAND_COLUMN = 2;
	private static final int CHECK_EXPRESSION_COLUMN = 3;

	private CaptureAppender appender;
    private List<FitCell> cells;

    /**
	 * Initializes a new {@code LogFixture} using a {@link LoggerProvider}
	 * and a {@link LogEventAnalyzerFactory}.
	 *
	 * @see #LogFixture(LoggerProvider, LogEventAnalyzerFactory)
	 * LogFixture(LoggerProvider, CellArgumentParserFactory, LogEventAnalyzerFactory)
	 */
	public LogFixture() {
		this(new LoggerProvider(), new LogEventAnalyzerFactory());
	}

	/**
	 * Initializes a new LogFixture.
	 *
	 * @param logs                    {@code LoggerProvider} to receive loggers.
	 * @param logEventAnalyzerFactory {@code LogEventAnalyzerFactory}
	 *                                to analyze log entries
	 */
	public LogFixture(final LoggerProvider logs,
	                  final LogEventAnalyzerFactory logEventAnalyzerFactory) {
		this.loggerProvider = logs;
		this.logEventAnalyzerFactory = logEventAnalyzerFactory;
	}

	/**
	 * Processes the table row {@code cells}.
	 *
	 * @param cells row to parse and process
	 */
	@Override
	protected void doCells(List<FitCell> cells) {
		this.cells = cells;
		this.appender = getAppender();

		if (appender != null) {
			try {
				executeCommand();
			} catch (final IllegalArgumentException e) {
				cells.get(COMMAND_COLUMN).exception("Illegal Format");
			}
		}
	}

	private void executeCommand() {
		Map<String, String> parameters =
				FitUtils.extractCellParameterMap(cells.get(COMMAND_COLUMN));
		String command = cells.get(COMMAND_COLUMN).getFitValue();
		getExpressionCellContent();

		dispatchCommand(command, parameters);
	}

	private String getExpressionCellContent() {
        FitCell cell = cells.get(CHECK_EXPRESSION_COLUMN);
		return validator.preProcess(cell);
	}

	private CaptureAppender getAppender() {
		String loggerName = cells.get(LOGGER_COLUMN).getFitValue();
		AppenderAttachable logger = getLogger(loggerName);

		if (logger == null) {
			cells.get(LOGGER_COLUMN).exception("Invalid logger");
			return null;
		}

		String appenderName = cells.get(APPENDER_COLUMN).getFitValue();
		String captureAppenderName = CaptureAppender.getAppenderNameFor(appenderName);

		final Appender appender = logger.getAppender(captureAppenderName);
		if (appender == null) {
			cells.get(APPENDER_COLUMN).exception("Invalid appender or appender not captured");
			return null;
		}
		return (CaptureAppender) appender;
	}

	private AppenderAttachable getLogger(String loggerName) {
		if ("rootLogger".equalsIgnoreCase(loggerName)) {
			return loggerProvider.getRootLogger();
		} else {
			return loggerProvider.getLogger(loggerName);
		}
	}

	private void dispatchCommand(String command, Map<String, String> parameters) {
        FitCell cell = cells.get(CHECK_EXPRESSION_COLUMN);
        LogEventAnalyzer analyzer = logEventAnalyzerFactory.getLogEventAnalyzerFor(
                validator, cell, appender.getAllEvents());

		if ("contains".equalsIgnoreCase(command)) {
			analyzer.processContains(parameters);
		} else if ("notContains".equalsIgnoreCase(command)) {
			analyzer.processNotContains(parameters);
		} else if ("containsException".equalsIgnoreCase(command)) {
			analyzer.processContainsException(parameters);
		} else if ("notContainsException".equalsIgnoreCase(command)) {
			analyzer.processNotContainsException(parameters);
		} else {
			cells.get(COMMAND_COLUMN).exception("unknown command");
		}
	}
}
