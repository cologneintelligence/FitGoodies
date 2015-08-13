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

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.Validator;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Map;

/**
 * Analyzes log events using an {@link AbstractLoggingEventMatcher}.
 */
public class LogEventAnalyzer {
	private final Counts counts;
	private final Validator validator;
	private final Parse cell;
	private final LoggingEvent[] events;

	/**
	 * Creates a new analyzer.
	 *
	 * @param counts        counts
	 * @param validator     validator for cell
	 * @param conditionCell cell which contains the reference value
	 * @param events        log events to process
	 */
	public LogEventAnalyzer(Counts counts, Validator validator, Parse conditionCell, LoggingEvent[] events) {
		this.counts = counts;
		this.validator = validator;
		this.cell = conditionCell;
		this.events = events;
	}

	/**
	 * Checks whether the log messages contain the expected exception.
	 *
	 * @param parameters parameters to filter the processing
	 *                   (see {@link AbstractLoggingEventMatcher}
	 */
	public void processNotContainsException(Map<String, String> parameters) {
		LoggingEvent match = getMessageWithException(parameters);

		if (match == null) {
			FitUtils.right(cell);
			counts.right++;
		} else {
			FitUtils.wrong(cell, match.getThrowableInformation().getThrowableStrRep()[0]);
			counts.wrong++;
		}
	}

	/**
	 * Checks whether the log messages do not contain the expected exception.
	 *
	 * @param parameters parameters to filter the processing
	 *                   (see {@link AbstractLoggingEventMatcher}
	 */
	public void processContainsException(Map<String, String> parameters) {
		LoggingEvent match = getMessageWithException(parameters);

		if (match == null) {
			FitUtils.wrong(cell);
			counts.wrong++;
		} else {
			FitUtils.right(cell);
			counts.right++;
			appendActualAndExpected(match.getThrowableInformation().getThrowableStrRep()[0]);
		}
	}

	private String getLowerCaseCheckExpression() {
		String resolvedContent = validator.preProcess(cell);
		cell.body = FitUtils.escape(resolvedContent);
		return resolvedContent.toLowerCase();
	}

	private LoggingEvent getMessageWithException(Map<String, String> parameters) {
		final String lowerCaseCheckExpression = getLowerCaseCheckExpression();

		return getMatchingEvent(new AbstractLoggingEventMatcher() {
			@Override
			boolean matches(LoggingEvent event) {
				ThrowableInformation errorInfo = event.getThrowableInformation();
				if (errorInfo == null) {
					return false;
				}

				return errorInfo.getThrowableStrRep()[0].toLowerCase()
						.contains(lowerCaseCheckExpression);
			}
		}, parameters);
	}

	/**
	 * Checks whether the log messages contain the expected text.
	 *
	 * @param parameters parameters to filter the processing
	 *                   (see {@link AbstractLoggingEventMatcher}
	 */
	public void processContains(Map<String, String> parameters) {
		LoggingEvent match = getMessageWithString(parameters);

		if (match == null) {
			FitUtils.wrong(cell);
			counts.wrong++;
		} else {
			FitUtils.right(cell);
			appendActualAndExpected(match.getMessage().toString());
			counts.right++;
		}
	}

	/**
	 * Checks whether the log messages do not contain the expected text.
	 *
	 * @param parameters parameters to filter the processing
	 *                   (see {@link AbstractLoggingEventMatcher}
	 */
	public void processNotContains(Map<String, String> parameters) {
		LoggingEvent match = getMessageWithString(parameters);

		if (match == null) {
			FitUtils.right(cell);
			counts.right++;
		} else {
			FitUtils.wrong(cell, match.getMessage().toString());
			counts.wrong++;

		}
	}

	private LoggingEvent getMessageWithString(Map<String, String> parameters) {
		final String checkExpression = getLowerCaseCheckExpression();
		return getMatchingEvent(new AbstractLoggingEventMatcher() {
			@Override
			boolean matches(LoggingEvent arg0) {
				return arg0.getRenderedMessage().toLowerCase().contains(checkExpression);
			}
		}, parameters);
	}

	private LoggingEvent getMatchingEvent(AbstractLoggingEventMatcher matcher,
	                                      Map<String, String> parameters) {
		return matcher.getFirstMatchingEvent(events, parameters);
	}

	private void appendActualAndExpected(String actual) {
		FitUtils.info(cell, "(expected)");
		cell.addToBody("<hr/>");
		cell.addToBody(actual);
		FitUtils.info(cell, "(actual)");
	}
}
