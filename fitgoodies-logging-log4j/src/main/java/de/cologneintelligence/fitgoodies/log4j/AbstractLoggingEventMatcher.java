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

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Map;

/**
 * Abstract class which decides which logging event matches given conditions.
 * The method {@link #matches(LoggingEvent)} must be overridden to provide
 * custom matches.
 * <p>
 * The class uses two parameters of the map: minlevel and thread. They are used
 * to pre-filter the matches. If minlevel is set, only log entries with this
 * level or higher are analyzed. If thread is set, only log entries produced by
 * the given thread are analyzed.
 */
public abstract class AbstractLoggingEventMatcher {
	/**
	 * Gets the first matching event which matches {@link #matches(LoggingEvent)}
	 * and the given parameters.
	 *
	 * @param events     log entries to process
	 * @param parameters filter parameters
	 * @return first matching log entry or {@code null} otherwise
	 */
	public final LoggingEvent getFirstMatchingEvent(final LoggingEvent[] events,
	                                                final Map<String, String> parameters) {

		for (LoggingEvent event : events) {
			if (matchesParameters(event, parameters) && matches(event)) {
				return event;
			}
		}
		return null;
	}

	private boolean matchesParameters(final LoggingEvent event,
	                                  final Map<String, String> parameters) {
		if (!isSelectedThread(event, parameters)) {
			return false;
		} else if (!isSelectedLevel(event, parameters)) {
			return false;
		}

		return true;
	}

	private boolean isSelectedLevel(final LoggingEvent event,
	                                final Map<String, String> parameters) {
		if (!parameters.containsKey("minlevel")) {
			return true;
		} else {
			return event.getLevel().isGreaterOrEqual(
					Level.toLevel(parameters.get("minlevel")));
		}
	}

	private boolean isSelectedThread(final LoggingEvent event,
	                                 final Map<String, String> parameters) {
		if (!parameters.containsKey("thread")) {
			return true;
		} else {
			return parameters.get("thread").equalsIgnoreCase(event.getThreadName());
		}
	}

	/**
	 * Called to check whether a event matches custom conditions.
	 *
	 * @param event event to process
	 * @return {@code true} iif the event matches
	 */
	abstract boolean matches(LoggingEvent event);
}
