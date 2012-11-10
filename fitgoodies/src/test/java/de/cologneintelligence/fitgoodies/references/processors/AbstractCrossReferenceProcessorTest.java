/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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


package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 *
 * @author jwierum
 */
public final class AbstractCrossReferenceProcessorTest extends FitGoodiesTestCase {
	private static class TestProcessor extends AbstractCrossReferenceProcessor {
		public TestProcessor(final String pattern) {
			super(pattern);
		}
		@Override public String info() { return null; }
		@Override public String processMatch(final CrossReference cr, final Object object)
				throws CrossReferenceProcessorShortcutException { return null; }
	}

	public void testExtraction() {
		TestProcessor proc = new TestProcessor("(x)\\(\\)");
		CrossReference cr = proc.extractCrossReference("x()");

		assertEquals("x", cr.getCommand());
		assertNull(cr.getNamespace());
		assertNull(cr.getParameter());

		proc = new TestProcessor("(y)\\(\\)");
		cr = proc.extractCrossReference("y()");

		assertEquals("y", cr.getCommand());
		assertNull(cr.getNamespace());
		assertNull(cr.getParameter());


		proc = new TestProcessor("(y)\\(([a-z]+)\\)");
		cr = proc.extractCrossReference("y(asdf)");

		assertEquals("y", cr.getCommand());
		assertEquals("asdf", cr.getParameter());
		assertNull(cr.getNamespace());

		proc = new TestProcessor("(x)\\(([0-9]+)\\)");
		cr = proc.extractCrossReference("x(123)");

		assertEquals("x", cr.getCommand());
		assertEquals("123", cr.getParameter());
		assertNull(cr.getNamespace());


		proc = new TestProcessor("([a-z]+)\\.(y)\\(([a-z]+)\\)");
		cr = proc.extractCrossReference("ns.y(asdf)");

		assertEquals("ns", cr.getNamespace());
		assertEquals("y", cr.getCommand());
		assertEquals("asdf", cr.getParameter());

		proc = new TestProcessor("([0-9]+)\\.(x)\\(([0-9]+)\\)");
		cr = proc.extractCrossReference("123.x(321)");

		assertEquals("123", cr.getNamespace());
		assertEquals("x", cr.getCommand());
		assertEquals("321", cr.getParameter());

		proc = new TestProcessor("(w)(x)(y)(z)");
		cr = proc.extractCrossReference("wxyz");
		assertNull(cr);

		proc = new TestProcessor("1234");
		cr = proc.extractCrossReference("1234");
		assertNull(cr);
	}
}
