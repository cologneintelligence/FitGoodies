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


package de.cologneintelligence.fitgoodies.references;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class CrossReferenceTest extends FitGoodiesTestCase {
	@Test
	public void testCrossReference() {
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
		assertThat(c1.getCommand(), is(equalTo("empty")));
		assertThat(c1.getNamespace(), is(nullValue()));
		assertThat(c1.getParameter(), is(nullValue()));
		assertThat(c1.getProcessor(), is(sameInstance(p)));

		CrossReference c2 = new CrossReference("put", "ns", "param", p);
		assertThat(c2.getCommand(), is(equalTo("put")));
		assertThat(c2.getNamespace(), is(equalTo("ns")));
		assertThat(c2.getParameter(), is(equalTo("param")));
		assertThat(c2.getProcessor(), is(sameInstance(p)));
	}
}
