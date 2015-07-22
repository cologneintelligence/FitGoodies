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


package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public final class ScientificDoubleTest extends FitGoodiesTestCase {
	@Test
	public void testEquals() {
		ScientificDouble two = new ScientificDouble(2.0f);
		ScientificDouble twofour = new ScientificDouble(2.4f);
		ScientificDouble seven = new ScientificDouble(7.0f);

		assertThat(two.equals(new ScientificDouble(2.0f)), is(true));
		assertThat(seven.equals(new ScientificDouble(7.0f)), is(true));
		assertThat(twofour.equals(new ScientificDouble(2.4f)), is(true));

		assertThat(seven.equals(new ScientificDouble(2.0f)), is(false));

		assertThat(twofour.equals(new ScientificDouble(7.0f)), is(false));

		assertThat(two.equals(new ScientificDouble(2.4f)), is(false));

		assertThat(new ScientificDouble(2.0f).hashCode(), is(equalTo((Object) two.hashCode())));
		assertThat(new ScientificDouble(2.4f).hashCode(), is(equalTo((Object) twofour.hashCode())));
		assertThat(new ScientificDouble(7.0f).hashCode(), is(equalTo((Object) seven.hashCode())));

		assertThat(two.hashCode(), is(not(seven.hashCode())));

		assertThat(two.hashCode(), is(twofour.hashCode()));
		assertThat(two.hashCode(), is(new ScientificDouble(2.4f).hashCode()));
	}

	@Test
	public void testScientificDouble() {
		Double pi = 3.141592865;
		assertEquals(pi, ScientificDouble.valueOf("3"));
		assertEquals(pi, ScientificDouble.valueOf("3.14"));
		assertEquals(pi, ScientificDouble.valueOf("3.142"));
		assertEquals(pi, ScientificDouble.valueOf("3.1416"));
		assertEquals(pi, ScientificDouble.valueOf("3.14159"));
		assertEquals(pi, ScientificDouble.valueOf("3.141592865"));

		assertNotEquals(ScientificDouble.valueOf("3.140"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.144"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.1414"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.141592863"), pi);

		assertEquals(6.02e23, ScientificDouble.valueOf("6.02e23"));
		assertEquals(6.024E23, ScientificDouble.valueOf("6.02E23"));
		assertEquals(6.016e23, ScientificDouble.valueOf("6.02e23"));

		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) CoreMatchers.is(not(CoreMatchers.equalTo(6.026e23))));
		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) CoreMatchers.is(not(CoreMatchers.equalTo(6.014e23))));
	}

	@Test
	public void testConverts() {
		ScientificDouble value1 = ScientificDouble.valueOf("25.31");
		ScientificDouble value2 = ScientificDouble.valueOf(".31");

		assertThat((double) value1.floatValue(), CoreMatchers.is(closeTo(25.31, 0.01)));
		assertThat((double) value2.floatValue(), CoreMatchers.is(closeTo(.31, 0.01)));

		assertThat(value1.doubleValue(), CoreMatchers.is(closeTo(25.31, 0.01)));
		assertThat(value2.doubleValue(), CoreMatchers.is(closeTo(.31, 0.01)));

		assertThat(value1.intValue(), CoreMatchers.is(25));
		assertThat(value2.intValue(), CoreMatchers.is(0));

		assertThat(value1.longValue(), CoreMatchers.is(25L));
		assertThat(value2.longValue(), CoreMatchers.is(0L));

		assertThat(value1.toString(), startsWith("25.3"));
		assertThat(value2.toString(), startsWith("0.3"));
	}

	private void assertNotEquals(ScientificDouble expected, Double actual) {
		assertThat(expected, (Matcher) is(not(equalTo(actual))));
	}

	private void assertEquals(Double expected, ScientificDouble actual) {
		assertThat(actual, (Matcher) is(equalTo(expected)));
	}
}
