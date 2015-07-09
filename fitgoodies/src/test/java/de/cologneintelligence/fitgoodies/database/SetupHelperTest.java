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


package de.cologneintelligence.fitgoodies.database;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class SetupHelperTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new SetupHelper();
    }

    @Test
    public void testProvider() throws Exception {
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        assertThat(DriverManager.getDriver("jdbc://test"), not(CoreMatchers.is(nullValue())));
    }

    @Test
    public void testUser() {
        helper.setUser("test");
        assertThat(helper.getUser(), is(equalTo("test")));
        helper.setUser("user");
        assertThat(helper.getUser(), is(equalTo("user")));
    }

    @Test
    public void testPassword() {
        helper.setPassword("pass");
        assertThat(helper.getPassword(), is(equalTo("pass")));
        helper.setPassword("pw2");
        assertThat(helper.getPassword(), is(equalTo("pw2")));
    }

    @Test
    public void testSetConnectionString() {
        helper.setConnectionString("text");
        assertThat(helper.getConnectionString(), is(equalTo("text")));
        helper.setConnectionString("t2");
        assertThat(helper.getConnectionString(), is(equalTo("t2")));
    }

    @Test
    public void testGetConnection() throws Exception {
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        helper.setUser("username");
        helper.setPassword("pw1");
        helper.setConnectionString("jdbc://test/url");
        final Connection conn = helper.getConnection();
        assertThat(conn, not(CoreMatchers.is(nullValue())));
        assertThat(conn, is(equalTo(DriverMock.getLastReturnedConnection())));
    }
}
