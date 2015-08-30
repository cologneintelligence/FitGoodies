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

import de.cologneintelligence.fitgoodies.RowFixture;
import de.cologneintelligence.fitgoodies.dynamic.ResultSetWrapper;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * This fixture takes a table name as an argument, fetches its content and
 * compares it with the given table. It's possible to filter the resulting
 * table using an optional where clause.
 * <p/>
 * <p/>
 * The credentials used to authenticate can be set in the code using the
 * {@link de.cologneintelligence.fitgoodies.database.SetupHelper} or using a
 * {@link de.cologneintelligence.fitgoodies.database.SetupFixture} in your HTML table.
 * <p/>
 * <p/>
 * The following table would compare the columns &quot;c1&quot; and &quot;c2&quot;
 * of the SQL table &quot;tab1&quot; with given the HTML table, if the value of
 * c1 is bigger than 10:
 * <p/>
 * <p/>
 * <table border="1" summary="">
 * <tr>
 * <td>fitgoodies.database.TableFixture</td>
 * <td>table=tab1</td>
 * <td>where=c1 &gt; 10</td>
 * </tr>
 * <tr><td>c1</td><td>c2</td></tr>
 * <tr><td>15</td><td>20</td></tr>
 * <tr><td>18</td><td>93</td></tr>
 * </table>
 */
public class TableFixture extends RowFixture {
	private Connection connection;
	private ResultSetWrapper resultSetWrapper;
	private Statement statement;
	public String table;
    public String where;

	private void connect() {
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
	 * @throws Exception propagated to fit.
	 */
	@Override
	public void tearDown() throws Exception {
		if (statement != null) {
			statement.close();
			statement = null;
		}
		super.tearDown();
	}

	@Override
	protected void doRows(List<FitRow> rows) throws Exception {
        connect();
        resultSetWrapper = new ResultSetWrapper(getResultSet());
		super.doRows(rows);
	}

	/**
	 * Generates a {@code java.sql.ResultSet} by using the saved connection.
	 * This method queries the table {@code tableName} and appends
	 * {@code where} as an optional where clause.
	 *
	 * @return the {@code ResultSet} which the query returned
	 */
	protected ResultSet getResultSet() {
		if (table == null) {
			throw new IllegalArgumentException("missing parameter: table");
		}

		String whereClause = "";
		if (where != null) {
            whereClause = " WHERE " + where;
        }

        try {
			statement = connection.createStatement();
			return statement.executeQuery("SELECT * FROM " + table + whereClause);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the type of the dynamic created target class.
	 *
	 * @return the type of the target class.
	 */
	@Override
	public final Class<?> getTargetClass() {
		return resultSetWrapper.getClazz();
	}

	/**
	 * Gets an array which represents the created ResultSet as an object array.
	 * The type of these objects can be determined via {@code getTargetClass()}.
	 *
	 * @throws java.lang.Exception
	 */
	@Override
	public final Object[] query() throws Exception {
		return resultSetWrapper.getRows();
	}

	/**
	 * Returns the extracted table name.
	 *
	 * @return the table name used by the fixture.
	 */
	public final String getTable() {
		return table;
	}

}
