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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountsTest {

	@Test
	public void testToString() throws Exception {
		Counts c1 = new Counts();
		c1.exceptions = 12;
		c1.right = 1;
		c1.wrong = 5;
		c1.ignores = 3;

		assertToString(c1);

		Counts c2 = new Counts();
		c2.exceptions = 7;
		c2.right = 7;
		c2.wrong = 7;
		c2.ignores = 7;

		assertToString(c2);
	}

	public void assertToString(Counts c) {
		assertThat(c.toString(), is(equalTo(
				String.format("%d right, %d wrong, %d ignored, %d exceptions",
						c.right, c.wrong, c.ignores, c.exceptions))));
	}

	@Test
	public void testTally() throws Exception {
		Counts c = new Counts();

		Counts c2 = new Counts();
		c2.exceptions = 7;
		c2.right = 7;
		c2.wrong = 7;
		c2.ignores = 7;

		c.tally(c2);

		assertThat(c.right, is(7));
		assertThat(c.wrong, is(7));

		c2.exceptions = 1;
		c2.right = 2;
		c2.wrong = 3;
		c2.ignores = 4;

		c.tally(c2);

		assertThat(c.right, is(9));
		assertThat(c.wrong, is(10));
		assertThat(c.exceptions, is(8));
		assertThat(c.ignores, is(11));
	}
}
