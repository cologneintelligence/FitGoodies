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

/**
 * Analyzer for log events.
 * The implementation should be initialized with a cell (expected) and a number
 * of log messages.
 *
 * @author jwierum
 * @version $Id$
 */
public interface LogEventAnalyzer {

	/**
	 * Checks whether the log messages contain the expected exception.
	 * @param parameters parameters to filter the processing
	 * 	(see {@link AbstractLoggingEventMatcher}
	 */
	void processNotContainsException(final Map<String, String> parameters);

	/**
	 * Checks whether the log messages do not contain the expected exception.
	 * @param parameters parameters to filter the processing
	 * 	(see {@link AbstractLoggingEventMatcher}
	 */
	void processContainsException(final Map<String, String> parameters);

	/**
	 * Checks whether the log messages contain the expected text.
	 * @param parameters parameters to filter the processing
	 * 	(see {@link AbstractLoggingEventMatcher}
	 */
	void processContains(final Map<String, String> parameters);

	/**
	 * Checks whether the log messages do not contain the expected text.
	 * @param parameters parameters to filter the processing
	 * 	(see {@link AbstractLoggingEventMatcher}
	 */
	void processNotContains(final Map<String, String> parameters);

}