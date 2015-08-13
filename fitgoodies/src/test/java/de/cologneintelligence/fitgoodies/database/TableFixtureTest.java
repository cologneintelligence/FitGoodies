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

import de.cologneintelligence.fitgoodies.types.ScientificDouble;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TableFixtureTest extends FitGoodiesTestCase {
    private TableFixture fixture;
    private SetupHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setUser("user");
        helper.setPassword("pw");
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        helper.setConnectionString("jdbc://test");

        fixture = new TableFixture();
    }

    @Test
    public void testGetResultSet() throws SQLException {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM table")).thenReturn(resultSet);

        fixture.setConnection(conn);
        final ResultSet actual = fixture.getResultSet("table", null);

        assertThat(actual, is(sameInstance(resultSet)));
    }

    @Test
    public void testGetResultSetWithWhere() throws SQLException {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM tbl2 WHERE x > 3")).thenReturn(resultSet);

        fixture.setConnection(conn);
        final ResultSet actual = fixture.getResultSet("tbl2", "x > 3");

        assertThat(actual, is(sameInstance(resultSet)));
    }

    @Test
    public void testDoTable() throws Exception {
        final Parse table = parseTable(
                tr("name", "age"),
                tr("Anika Hanson", "30"));


        fixture.setParams(new String[]{"table=tbl7"});
        ResultSetMockGenerator mocker = new ResultSetMockGenerator(
                "tbl7",
                new String[]{"name", "age"},
                new Object[][]{
                        new Object[]{"Angela Bennett", 32},
                        new Object[]{"Anika Hanson", 30}});

        fixture.setConnection(mocker.getConnection());
        fixture.doTable(table);
        assertThat(fixture.getTable(), is(equalTo("tbl7")));

        final Class<?> c = fixture.getTargetClass();

        assertThat(c.getField("name").getType(), (Matcher) is(sameInstance(String.class)));
        assertThat(c.getField("age").getType(), (Matcher) is(sameInstance(Integer.class)));

        assertThat(fixture.counts().wrong, is(equalTo((Object) 1)));
        assertThat(fixture.counts().right, is(equalTo((Object) 2)));

        mocker.verifyInteractions();
    }

    @Test
    public void testDoTable2() throws Exception {
        final Parse table = parseTable(tr("age"), tr("42.3"));

        fixture.setParams(new String[]{"table=table1"});
        ResultSetMockGenerator mocker = new ResultSetMockGenerator(
                "table1",
                new String[]{"age"},
                new Object[][]{
                        new Object[]{42.3}
                });

        fixture.setConnection(mocker.getConnection());
        fixture.doTable(table);
        assertThat(fixture.getTable(), is(equalTo("table1")));

        final Class<?> c = fixture.getTargetClass();
        assertThat(c.getField("age").getType(), (Matcher) is(sameInstance(ScientificDouble.class)));
        assertThat(fixture.counts().right, is(equalTo((Object) 1)));
        mocker.verifyInteractions();
    }

    @Test
    public void testConstructor() {
        ResultSetMockGenerator mocker = new ResultSetMockGenerator(
                null,
                new String[]{},
                new Object[][]{}
        );

        fixture = new TableFixture(mocker.getConnection());
        assertThat(fixture.getConnection(), is(sameInstance(mocker.getConnection())));
    }

    @Test
    public void testHelperInteraction() throws Exception {
        assertThat(fixture.getConnection(), is(sameInstance(helper.getConnection())));
        DriverMock.cleanup();
    }

    @Test
    public void testWhereClause() throws Exception {
        final Parse table = parseTable(tr("ignore"), tr("x"));

        ResultSetMockGenerator mocker = new ResultSetMockGenerator(
                "tbl4", "x > 7",
                new String[]{},
                new Object[][]{});

        fixture.setConnection(mocker.getConnection());
        fixture.setParams(new String[]{"table=tbl4", "where=x > 7"});
        fixture.doTable(table);
        fixture.getConnection();
        mocker.verifyInteractions();
    }

    @Test
    public void testError() throws Exception {
        final Parse table = parseTable(tr("ignore"), tr("x"));

        fixture.doTable(table);
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
    }
}
