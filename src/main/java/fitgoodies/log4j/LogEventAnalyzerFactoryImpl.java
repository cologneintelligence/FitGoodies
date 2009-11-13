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

import org.apache.log4j.spi.LoggingEvent;

import fit.Fixture;
import fit.Parse;

/**
 * Implementation of LogEventAnalyzerFactory which provides {@link LogEventAnalyzerImpl}
 * objects.
 *
 * @author jwierum
 * @version $Id$
 */
public final class LogEventAnalyzerFactoryImpl implements LogEventAnalyzerFactory {
	@Override
	public LogEventAnalyzer getLogEventAnalyzerFor(final Fixture parent,
			final Parse conditionCell, final LoggingEvent[] events) {
		return new LogEventAnalyzerImpl(parent, conditionCell, events);
	}

}
