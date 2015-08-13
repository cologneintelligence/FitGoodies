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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public final class SimpleRegexFilterTest extends FitGoodiesTestCase {
	@Test
	public void testConstructor() {
		SimpleRegexFilter filter = new SimpleRegexFilter("xy");
		assertThat(filter.getPattern(), is(equalTo("xy")));

		filter = new SimpleRegexFilter(".*\\.txt");
		assertThat(filter.getPattern(), is(equalTo(".*\\.txt")));
	}

	@Test(expected = RuntimeException.class)
	public void testNullConstructor() {
		new SimpleRegexFilter(null);
	}

	private File makeFile(String name, boolean isFile) {
		File file = mock(File.class);
		when(file.getName()).thenReturn(name);
		when(file.isFile()).thenReturn(isFile);
		when(file.isDirectory()).thenReturn(!isFile);
		return file;
	}

	@Test
	public void testAccept() {
		SimpleRegexFilter filter = new SimpleRegexFilter(".*\\.txt");

		assertThat(filter.accept(makeFile("dir.txt", false)), is(false));
		assertThat(filter.accept(makeFile("other dir.txt", false)), is(false));

		assertThat(filter.accept(makeFile("test.txt", true)), is(true));
		assertThat(filter.accept(makeFile("file.txt", true)), is(true));
		assertThat(filter.accept(makeFile("test.java", true)), is(false));

		filter = new SimpleRegexFilter(".*\\.java");
		assertThat(filter.accept(makeFile("test.txt", true)), is(false));
		assertThat(filter.accept(makeFile("test.java", true)), is(true));
	}
}
