/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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


package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class FileCountTest extends FitGoodiesTestCase {
	@Test
	public void testGetters() {
		Counts expectedCounts = new Counts();
		File expectedFile = new File("x");
		FileCount actual = new FileCount(expectedFile, expectedCounts);
		assertThat(actual.getFile(), is(equalTo(expectedFile)));
		assertThat(actual.getCounts(), is(sameInstance(expectedCounts)));

		expectedCounts = new Counts();
		expectedFile = new File("y");
		actual = new FileCount(expectedFile, expectedCounts);
		assertThat(actual.getFile(), is(equalTo(expectedFile)));
		assertThat(actual.getCounts(), is(sameInstance(expectedCounts)));
	}

	@Test
	public void testEquals() {
		Counts counts = new Counts();
		FileCount fc1 = new FileCount(new File("asdf"), counts);
		FileCount fc2 = new FileCount(new File("fdsa"), counts);

		assertThat(fc1.equals(fc2), is(false));

		fc1 = new FileCount(new File("asdf"), counts);
		fc2 = new FileCount(new File("asdf"), counts);
		assertThat(fc1.equals(fc2), is(true));

		fc1 = new FileCount(new File("x"), counts);
		fc2 = new FileCount(new File("y"), counts);
		assertThat(fc1.equals(fc2), is(false));

		fc1 = new FileCount(new File("a"), counts);
		fc2 = new FileCount(new File("a"), counts);
		assertThat(fc1.equals(fc2), is(true));

		fc1 = new FileCount(new File("a"), counts);
		assertThat(fc1, (Matcher) not(equalTo("a")));
	}

	@Test
	public void testHash() {
		Counts counts = new Counts();
		FileCount fc1 = new FileCount(new File("asdf"), counts);
		FileCount fc2 = new FileCount(new File("fdsa"), counts);

		assertThat(fc1.hashCode(), is(not(fc2.hashCode())));

		fc1 = new FileCount(new File("asdf"), counts);
		fc2 = new FileCount(new File("asdf"), counts);
		assertThat(fc1.hashCode(), is(fc2.hashCode()));

		fc1 = new FileCount(new File("x"), counts);
		fc2 = new FileCount(new File("y"), counts);
		assertThat(fc1.hashCode(), is(not(fc2.hashCode())));

		fc1 = new FileCount(new File("a"), counts);
		fc2 = new FileCount(new File("a"), counts);
		assertThat(fc1.hashCode(), is(fc2.hashCode()));
	}
}
