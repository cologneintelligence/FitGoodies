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

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.log4j.LogEventAnalyzer;
import de.cologneintelligence.fitgoodies.log4j.LogEventAnalyzerFactory;
import de.cologneintelligence.fitgoodies.log4j.LogEventAnalyzerFactoryImpl;
import de.cologneintelligence.fitgoodies.log4j.LogEventAnalyzerImpl;

/**
 * @author jwierum
 * @version $Id$
 *
 */
public final class LogEventAnalyzerFactoryImplTest extends FitGoodiesTestCase {
	public void testReturnType() {
		LogEventAnalyzerFactory factory = new LogEventAnalyzerFactoryImpl();
		LogEventAnalyzer analyzer = factory.getLogEventAnalyzerFor(null,
				null, null);

		assertEquals(LogEventAnalyzerImpl.class, analyzer.getClass());
	}
}
