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

package de.cologneintelligence.fitgoodies.log4j;

import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import fit.Fixture;
import fit.Parse;

/**
 * Analyzes log events using an {@link AbstractLoggingEventMatcher}.
 *
 * @author jwierum
 * @version $Id$
 */
public final class LogEventAnalyzerImpl implements LogEventAnalyzer {
	private final Fixture parent;
	private final Parse cell;
	private final LoggingEvent[] events;

	/**
	 * Creates a new analyzer.
	 * @param parent parent fixture
	 * @param conditionCell cell which contains the reference value
	 * @param events log events to process
	 */
	public LogEventAnalyzerImpl(final Fixture parent, final Parse conditionCell,
			final LoggingEvent[] events) {
		this.parent = parent;
		this.cell = conditionCell;
		this.events = events;
	}

	@Override
	public void processNotContainsException(
			final Map<String, String> parameters) {
		LoggingEvent match = getMessageWithException(parameters);

		if (match == null) {
			markCellAsRight();
		} else {
			markCellAsWrong(match.getThrowableInformation().getThrowableStrRep()[0]);
		}
	}

	@Override
	public void processContainsException(final Map<String, String> parameters) {
		LoggingEvent match = getMessageWithException(parameters);

		if (match == null) {
			markCellAsWrong();
		} else {
			markCellAsRight(match.getThrowableInformation().getThrowableStrRep()[0]);
		}
	}

	private String getLowerCaseCheckExpression() {
		return cell.text().toLowerCase();
	}

	private LoggingEvent getMessageWithException(
			final Map<String, String> parameters) {
		final String lowerCaseCheckExpression = getLowerCaseCheckExpression();
		return getMatchingEvent(new AbstractLoggingEventMatcher() {
			@Override boolean matches(final LoggingEvent arg0) {
				ThrowableInformation errorInfo = arg0.getThrowableInformation();
				if (errorInfo != null) {
					return errorInfo.getThrowableStrRep()[0].toLowerCase()
						.contains(lowerCaseCheckExpression);
				}
				return false;
			}
		}, parameters);
	}

	@Override
	public void processContains(final Map<String, String> parameters) {
		LoggingEvent match = getMessageWithString(parameters);

		if (match == null) {
			markCellAsWrong();
		} else {
			markCellAsRight(match.getMessage().toString());
		}
	}

	@Override
	public void processNotContains(final Map<String, String> parameters) {
		LoggingEvent match = getMessageWithString(parameters);

		if (match == null) {
			markCellAsRight();
		} else {
			markCellAsWrong(match.getMessage().toString());
		}
	}

	private LoggingEvent getMessageWithString(
			final Map<String, String> parameters) {
		final String checkExpression = getLowerCaseCheckExpression();
		return getMatchingEvent(new AbstractLoggingEventMatcher() {
			@Override boolean matches(final LoggingEvent arg0) {
				return arg0.getRenderedMessage().toLowerCase().contains(checkExpression);
			}
		}, parameters);
	}

	private LoggingEvent getMatchingEvent(final AbstractLoggingEventMatcher matcher,
			final Map<String, String> parameters) {
		return matcher.getFirstMatchingEvent(events, parameters);
	}

	private void markCellAsRight() {
		parent.right(cell);
	}

	private void markCellAsRight(final String actual) {
		parent.right(cell);
		appendActualAndExpected(actual, cell);
	}

	private void markCellAsWrong() {
		parent.wrong(cell);
	}

	private void markCellAsWrong(final String actual) {
		parent.wrong(cell);
		appendActualAndExpected(actual, cell);
	}

	private void appendActualAndExpected(final String actual, final Parse cell) {
		parent.info(cell, "(expected)");
		cell.addToBody("<hr/>");
		cell.addToBody(actual);
		parent.info(cell, "(actual)");
	}
}
