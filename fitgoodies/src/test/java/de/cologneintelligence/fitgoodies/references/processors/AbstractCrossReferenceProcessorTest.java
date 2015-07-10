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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


public final class AbstractCrossReferenceProcessorTest extends FitGoodiesTestCase {
	private static class TestProcessor extends AbstractCrossReferenceProcessor {
		public TestProcessor(final String pattern) {
			super(pattern);
		}
		@Override public String info() { return null; }
		@Override public String processMatch(final CrossReference cr, final Object object)
				throws CrossReferenceProcessorShortcutException { return null; }
	}

	@Test
	public void testExtraction() {
		TestProcessor proc = new TestProcessor("(x)\\(\\)");
		CrossReference cr = proc.extractCrossReference("x()");

		assertThat(cr.getCommand(), is(equalTo("x")));
		assertThat(cr.getNamespace(), is(nullValue()));
		assertThat(cr.getParameter(), is(nullValue()));

		proc = new TestProcessor("(y)\\(\\)");
		cr = proc.extractCrossReference("y()");

		assertThat(cr.getCommand(), is(equalTo("y")));
		assertThat(cr.getNamespace(), is(nullValue()));
		assertThat(cr.getParameter(), is(nullValue()));


		proc = new TestProcessor("(y)\\(([a-z]+)\\)");
		cr = proc.extractCrossReference("y(asdf)");

		assertThat(cr.getCommand(), is(equalTo("y")));
		assertThat(cr.getParameter(), is(equalTo("asdf")));
		assertThat(cr.getNamespace(), is(nullValue()));

		proc = new TestProcessor("(x)\\(([0-9]+)\\)");
		cr = proc.extractCrossReference("x(123)");

		assertThat(cr.getCommand(), is(equalTo("x")));
		assertThat(cr.getParameter(), is(equalTo("123")));
		assertThat(cr.getNamespace(), is(nullValue()));


		proc = new TestProcessor("([a-z]+)\\.(y)\\(([a-z]+)\\)");
		cr = proc.extractCrossReference("ns.y(asdf)");

		assertThat(cr.getNamespace(), is(equalTo("ns")));
		assertThat(cr.getCommand(), is(equalTo("y")));
		assertThat(cr.getParameter(), is(equalTo("asdf")));

		proc = new TestProcessor("([0-9]+)\\.(x)\\(([0-9]+)\\)");
		cr = proc.extractCrossReference("123.x(321)");

		assertThat(cr.getNamespace(), is(equalTo("123")));
		assertThat(cr.getCommand(), is(equalTo("x")));
		assertThat(cr.getParameter(), is(equalTo("321")));

		proc = new TestProcessor("(w)(x)(y)(z)");
		cr = proc.extractCrossReference("wxyz");
		assertThat(cr, is(nullValue()));

		proc = new TestProcessor("1234");
		cr = proc.extractCrossReference("1234");
		assertThat(cr, is(nullValue()));
	}
}
