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

package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FileFixtureTest extends FitGoodiesTestCase {
	@Test
	public void testErrors() {
		Parse table = parseTable(
				tr("too short"),
				tr("wrong", "value"));

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 2)));
	}

	@Test
	public void testPattern() {
		Parse table = parseTable(tr("pattern", ".*\\.txt"));

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
		assertThat(helper.getPattern(), is(equalTo(".*\\.txt")));

		table = parseTable(tr("pattern", "testfile"));

		fixture.doTable(table);

		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(helper.getPattern(), is(equalTo("testfile")));
	}

	@Test
	public void testEncoding() {
		Parse table = parseTable(
				tr("directory", "dir"),
				tr("encoding", "utf-8"));

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);

		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(helper.getEncoding(), is(equalTo("utf-8")));

		table = parseTable(
				tr("directory", "c:\\"),
				tr("encoding", "latin-1"));

		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(helper.getEncoding(), is(equalTo("latin-1")));
	}
}
