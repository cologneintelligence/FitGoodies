/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.references;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 * $Id$
 * @author jwierum
 */

public class CrossReferenceTest extends FitGoodiesTestCase {
	public final void testCrossReference() {
		AbstractCrossReferenceProcessor p = new AbstractCrossReferenceProcessor(null) {
			@Override public String getPattern() {
				return null;
			}

			@Override public String processMatch(final CrossReference cr,
					final Object object) {
				return null;
			}

			@Override public String info() { return null; }
		};

		CrossReference c1 = new CrossReference("empty", null, null, p);
		assertEquals("empty", c1.getCommand());
		assertNull(c1.getNamespace());
		assertNull(c1.getParameter());
		assertSame(p, c1.getProcessor());

		CrossReference c2 = new CrossReference("put", "ns", "param", p);
		assertEquals("put", c2.getCommand());
		assertEquals("ns", c2.getNamespace());
		assertEquals("param", c2.getParameter());
		assertSame(p, c2.getProcessor());
	}
}
