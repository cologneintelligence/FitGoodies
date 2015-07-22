/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

package fit;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class ScientificDoubleTest {

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

		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) is(not(equalTo(6.026e23))));
		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) is(not(equalTo(6.014e23))));
	}

	@Test
	public void testConverts() {
		ScientificDouble value1 = ScientificDouble.valueOf("25.31");
		ScientificDouble value2 = ScientificDouble.valueOf(".31");

		assertThat((double) value1.floatValue(), is(closeTo(25.31, 0.01)));
		assertThat((double) value2.floatValue(), is(closeTo(.31, 0.01)));

		assertThat(value1.doubleValue(), is(closeTo(25.31, 0.01)));
		assertThat(value2.doubleValue(), is(closeTo(.31, 0.01)));

		assertThat(value1.intValue(), is(25));
		assertThat(value2.intValue(), is(0));

		assertThat(value1.longValue(), is(25L));
		assertThat(value2.longValue(), is(0L));

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
