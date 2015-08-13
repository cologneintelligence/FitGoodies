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

package de.cologneintelligence.fitgoodies.database;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
	private SetupHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = DependencyManager.getOrCreate(SetupHelper.class);
	}

	@Test
	public void testHelperInteraction1() throws Exception {
		final Parse table = parseTable(
				tr("provider", "de.cologneintelligence.fitgoodies.database.DriverMock"),
				tr("user", "username"),
				tr("password", "pass"),
				tr("connectionString", "db"));

		final SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);

		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(helper.getUser(), is(equalTo("username")));
		assertThat(helper.getPassword(), is(equalTo("pass")));
		assertThat(helper.getConnectionString(), is(equalTo("db")));
		assertThat(DriverManager.getDriver("jdbc://test"), not(CoreMatchers.is(nullValue())));
	}

	@Test
	public void testHelperInteraction2() throws Exception {
		final Parse table = parseTable(
				tr("provider", "de.cologneintelligence.fitgoodies.database.DriverMock"),
				tr("user", "user2"),
				tr("password", "pw2"),
				tr("connectionString", "jdbc://test/db"));

		final SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);

		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(helper.getUser(), is(equalTo("user2")));
		assertThat(helper.getPassword(), is(equalTo("pw2")));
		assertThat(helper.getConnectionString(), is(equalTo("jdbc://test/db")));
		assertThat(DriverManager.getDriver("jdbc://test"), not(CoreMatchers.is(nullValue())));
	}
}
