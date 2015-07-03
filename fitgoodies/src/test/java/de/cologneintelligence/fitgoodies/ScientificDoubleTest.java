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
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

		// This would be great!
		// But how to achieve this while supporting precisions?
		/*
		assertNotEquals(two.hashCode(), seven.hashCode());
		assertNotEquals(twofour.hashCode(), two.hashCode());
		assertNotEquals(seven.hashCode(), two.hashCode());
		*/
	}

	@Test
	public void testConstructor() {
		assertThat(new ScientificDouble(2, 1).equals(new ScientificDouble(2.9)), is(true));
		assertThat(new ScientificDouble(2, 0.3).equals(new ScientificDouble(2.9)), is(false));

		assertThat(new ScientificDouble(7, 0.5).equals(new ScientificDouble(7.2)), is(true));
		assertThat(new ScientificDouble(7, 0.3).equals(new ScientificDouble(7.6)), is(false));

	}
}
