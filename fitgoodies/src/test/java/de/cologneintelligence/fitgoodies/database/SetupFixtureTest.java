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

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesFixtureTestCase<SetupFixture> {
	private SetupHelper helper;

    @Override
    protected Class<SetupFixture> getFixtureClass() {
        return SetupFixture.class;
    }

	@Before
	public void setUp() throws Exception {
		helper = DependencyManager.getOrCreate(SetupHelper.class);
	}

	@Test
	public void testHelperInteraction1() throws Exception {
		useTable(
				tr("provider", "$class"),
				tr("user", "$username"),
				tr("password", "pass"),
				tr("connectionString", "db"));

        preparePreprocessWithConversion(String.class, "$class", "de.cologneintelligence.fitgoodies.database.DriverMock");
        preparePreprocessWithConversion(String.class, "$username", "username");
        preparePreprocessWithConversion(String.class, "pass", "pass");
        preparePreprocessWithConversion(String.class, "db", "db");

		run();

        assertCounts(0, 0, 0, 0);
		assertThat(helper.getUser(), is(equalTo("username")));
		assertThat(helper.getPassword(), is(equalTo("pass")));
		assertThat(helper.getConnectionString(), is(equalTo("db")));
		assertThat(DriverManager.getDriver("jdbc://test"), not(CoreMatchers.is(nullValue())));
	}

	@Test
	public void testHelperInteraction2() throws Exception {
		useTable(
				tr("provider", "a"),
				tr("user", "b"),
				tr("password", "c"),
				tr("connectionString", "d"));

        preparePreprocessWithConversion(String.class, "a", "de.cologneintelligence.fitgoodies.database.DriverMock");
        preparePreprocessWithConversion(String.class, "b", "user2");
        preparePreprocessWithConversion(String.class, "c", "pw2");
        preparePreprocessWithConversion(String.class, "d", "jdbc://test/db");


        run();

		assertCounts(0, 0, 0, 0);
		assertThat(helper.getUser(), is(equalTo("user2")));
		assertThat(helper.getPassword(), is(equalTo("pw2")));
		assertThat(helper.getConnectionString(), is(equalTo("jdbc://test/db")));
		assertThat(DriverManager.getDriver("jdbc://test"), not(CoreMatchers.is(nullValue())));
	}

}
