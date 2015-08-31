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

import de.cologneintelligence.fitgoodies.Validator;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Factory interface to provide {@link LogEventAnalyzer}s.
 */
public class LogEventAnalyzerFactory {
    /**
     * Creates a LogEventAnalyzer which is capable to analyze the events {@code
     * events} using the condition defined in {@code conditionCell}.
     *
     * @param validator     the validator for the cell
     * @param conditionCell cell which contains the condition
     * @param events        list of events to process   @return instance of LogEventAnalyzer
     * @return A LogEventAnalyzer able to processing all parameters.
     */
    public LogEventAnalyzer getLogEventAnalyzerFor(Validator validator, FitCell conditionCell,
                                                   LoggingEvent[] events) {
        return new LogEventAnalyzer(validator, conditionCell, events);
    }

}
