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

import org.apache.log4j.spi.LoggingEvent;

import fit.Fixture;
import fit.Parse;

/**
 * Factory interface to provide {@link LogEventAnalyzer}s.
 *
 */
public interface LogEventAnalyzerFactory {
	/**
	 * Creates a LogEventAnalyzer which is capable to analyze the events <code>
	 * events</code> using the condition defined in <code>conditionCell</code>.
	 *
	 * @param parent calling fixture
	 * @param conditionCell cell which contains the condition
	 * @param events list of events to process
	 * @return instance of LogEventAnalyzer
	 */
	LogEventAnalyzer getLogEventAnalyzerFor(Fixture parent, Parse conditionCell,
			LoggingEvent[] events);
}
