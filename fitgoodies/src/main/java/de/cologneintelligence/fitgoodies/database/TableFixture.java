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

import de.cologneintelligence.fitgoodies.RowFixture;
import de.cologneintelligence.fitgoodies.dynamic.ResultSetWrapper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Parse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This fixture takes a table name as an argument, fetches its content and
 * compares it with the given table. It's possible to filter the resulting
 * table using an optional where clause.
 * <p>
 *
 * The credentials used to authenticate can be set in the code using the
 * {@link de.cologneintelligence.fitgoodies.database.SetupHelper} or using a
 * {@link de.cologneintelligence.fitgoodies.database.SetupFixture} in your HTML table.
 * <p>
 *
 * The following table would compare the columns &quot;c1&quot; and &quot;c2&quot;
 * of the SQL table &quot;tab1&quot; with given the HTML table, if the value of
 * c1 is bigger than 10:
 * <p>
 *
 * <table border="1" summary="">
 * <tr>
 * 		<td>fitgoodies.database.TableFixture</td>
 * 		<td>table=tab1</td>
 * 		<td>where=c1 > 10</td>
 * </tr>
 * <tr><td>c1</td><td>c2</td></tr>
 * <tr><td>15</td><td>20</td></tr>
 * <tr><td>18</td><td>93</td></tr>
 * </table>
 *
 */
public class TableFixture extends RowFixture {
    private String table;
    private Connection connection;
    private ResultSetWrapper resultSetWrapper;
    private Statement statement;

    /**
     * Creates a new fixture. This constructor fetches a connection via
     * {@link de.cologneintelligence.fitgoodies.database.SetupHelper#getConnection()}, so it is
     * important to setup the connection details first.
     */
    public TableFixture() {
        try {
            final SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
            connection = helper.getConnection();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the SQL statement.
     *
     * @exception Exception propagated to fit.
     */
    @Override
    public void tearDown() throws Exception {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        super.tearDown();
    }

    /**
     * Creates a new fixture ignoring the values set in {@link SetupHelper} but
     * using {@code conn} as the connection object.
     * This constructor is primary used for testing.
     * @param conn the connection object
     */
    public TableFixture(final Connection conn) {
        connection = conn;
    }

    /**
     * Processes a HTML table. This method is called by fit.
     * @param parsedTable the table to process
     */
    @Override
    public void doTable(final Parse parsedTable) {
        try {
            final CrossReferenceHelper crossReferenceHelper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
            resultSetWrapper = new ResultSetWrapper(
                    getResultSet(
                            FixtureTools.getArg(getArgs(), "table", null, crossReferenceHelper),
                            FixtureTools.getArg(getArgs(), "where", null, crossReferenceHelper)));
        } catch (final RuntimeException e) {
            exception(parsedTable.parts.parts, e);
            return;
        } catch (final SQLException e) {
            exception(parsedTable.parts.parts, e);
            return;
        }
        super.doTable(parsedTable);
    }

    /**
     * Generates a {@code java.sql.ResultSet} by using the saved connection.
     * This method queries the table {@code tableName} and appends
     * {@code where} as an optional where clause.
     * @param tableName the table name to query
     * @param where a where clause or {@code null}
     * @return the {@code ResultSet} which the query returned
     */
    public final ResultSet getResultSet(final String tableName, final String where) {
        if (tableName == null) {
            throw new IllegalArgumentException("missing parameter: table");
        }

        String whereClause = "";
        if (where != null) {
            whereClause = " WHERE " + where;
        }

        try {
            final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
            table = helper.parseBody(tableName, "");

            statement = connection.createStatement();
            final ResultSet rs = statement.executeQuery("SELECT * FROM "
                    + table + whereClause);
            return rs;

        } catch (final CrossReferenceProcessorShortcutException e) {
            throw new RuntimeException(e);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the type of the dynamic created target class.
     * @return the type of the target class.
     */
    @Override
    public final Class<?> getTargetClass() {
        return resultSetWrapper.getClazz();
    }

    /**
     * Gets an array which represents the created ResultSet as an object array.
     * The type of these objects can be determined via {@code getTargetClass()}.
     */
    @Override
    public final Object[] query() throws Exception {
        return resultSetWrapper.getRows();
    }

    /**
     * Returns the extracted table name.
     * @return the table name used by the fixture.
     */
    public final String getTable() {
        return table;
    }

    /**
     * Sets the database connection to {@code conn}.
     * This method is primary used for testing.
     * @param conn connection to use.
     * @see #getConnection() getConnection()
     */
    final void setConnection(final Connection conn) {
        connection = conn;
    }

    /**
     * Gets the connection object which is used to generate the {@code ResultSet}.
     * @return the connection object
     * @see #setConnection(Connection) setConnection(Connection)
     */
    public final Connection getConnection() {
        return connection;
    }
}
