/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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

import java.sql.DriverManager;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

/**
 * $Id$
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        helper = DependencyManager.INSTANCE.getOrCreate(SetupHelper.class);
    }

    public final void testHelperInteraction1() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>provider</td><td>"
                + "de.cologneintelligence.fitgoodies.database.DriverMock</td></tr>"
                + "<tr><td>user</td><td>username</td></tr>"
                + "<tr><td>password</td><td>pass</td></tr>"
                + "<tr><td>connectionString</td><td>db</td></tr>"
                + "</table>"
                );

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        assertEquals("username", helper.getUser());
        assertEquals("pass", helper.getPassword());
        assertEquals("db", helper.getConnectionString());
        assertNotNull(DriverManager.getDriver("jdbc://test"));
    }

    public final void testHelperInteraction2() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>provider</td><td>"
                + "de.cologneintelligence.fitgoodies.database.DriverMock</td></tr>"
                + "<tr><td>user</td><td>user2</td></tr>"
                + "<tr><td>password</td><td>pw2</td></tr>"
                + "<tr><td>connectionString</td><td>jdbc://test/db</td></tr>"
                + "</table>"
                );

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        assertEquals("user2", helper.getUser());
        assertEquals("pw2", helper.getPassword());
        assertEquals("jdbc://test/db", helper.getConnectionString());
        assertNotNull(DriverManager.getDriver("jdbc://test"));
    }
}
