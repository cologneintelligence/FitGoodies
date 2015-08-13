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


package de.cologneintelligence.fitgoodies.types;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class ScientificDoubleTest extends FitGoodiesTestCase {
	@Test
	public void testEquals() {
		ScientificDouble two = new ScientificDouble(2.0f);
		ScientificDouble twofour = new ScientificDouble(2.4f);
		ScientificDouble seven = new ScientificDouble(7.0f);

		Assert.assertThat(two.equals(new ScientificDouble(2.0f)), CoreMatchers.is(true));
		Assert.assertThat(seven.equals(new ScientificDouble(7.0f)), CoreMatchers.is(true));
		Assert.assertThat(twofour.equals(new ScientificDouble(2.4f)), CoreMatchers.is(true));

		Assert.assertThat(seven.equals(new ScientificDouble(2.0f)), CoreMatchers.is(false));

		Assert.assertThat(twofour.equals(new ScientificDouble(7.0f)), CoreMatchers.is(false));

		Assert.assertThat(two.equals(new ScientificDouble(2.4f)), CoreMatchers.is(false));

		Assert.assertThat(new ScientificDouble(2.0f).hashCode(), CoreMatchers.is(CoreMatchers.equalTo((Object) two.hashCode())));
		Assert.assertThat(new ScientificDouble(2.4f).hashCode(), CoreMatchers.is(CoreMatchers.equalTo((Object) twofour.hashCode())));
		Assert.assertThat(new ScientificDouble(7.0f).hashCode(), CoreMatchers.is(CoreMatchers.equalTo((Object) seven.hashCode())));

		Assert.assertThat(two.hashCode(), CoreMatchers.is(CoreMatchers.not(seven.hashCode())));

		Assert.assertThat(two.hashCode(), CoreMatchers.is(twofour.hashCode()));
		Assert.assertThat(two.hashCode(), CoreMatchers.is(new ScientificDouble(2.4f).hashCode()));
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

		Assert.assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(6.026e23))));
		Assert.assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(6.014e23))));
	}

	@Test
	public void testConverts() {
		ScientificDouble value1 = ScientificDouble.valueOf("25.31");
		ScientificDouble value2 = ScientificDouble.valueOf(".31");

		Assert.assertThat((double) value1.floatValue(), CoreMatchers.is(Matchers.closeTo(25.31, 0.01)));
		Assert.assertThat((double) value2.floatValue(), CoreMatchers.is(Matchers.closeTo(.31, 0.01)));

		Assert.assertThat(value1.doubleValue(), CoreMatchers.is(Matchers.closeTo(25.31, 0.01)));
		Assert.assertThat(value2.doubleValue(), CoreMatchers.is(Matchers.closeTo(.31, 0.01)));

		Assert.assertThat(value1.intValue(), CoreMatchers.is(25));
		Assert.assertThat(value2.intValue(), CoreMatchers.is(0));

		Assert.assertThat(value1.longValue(), CoreMatchers.is(25L));
		Assert.assertThat(value2.longValue(), CoreMatchers.is(0L));

		Assert.assertThat(value1.toString(), Matchers.startsWith("25.3"));
		Assert.assertThat(value2.toString(), Matchers.startsWith("0.3"));
	}

	private void assertNotEquals(ScientificDouble expected, Double actual) {
		Assert.assertThat(expected, (Matcher) CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(actual))));
	}

	private void assertEquals(Double expected, ScientificDouble actual) {
		Assert.assertThat(actual, (Matcher) CoreMatchers.is(CoreMatchers.equalTo(expected)));
	}
}
