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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jmock.Expectations;
import org.jmock.Mockery;

/*
 *
 * @author jwierum
 */
public final class ResultSetMockGenerator {
	private ResultSetMockGenerator() {
	}

	public static ResultSet mkResultSet(final Mockery context,
			final String[] cols, final Object[][] obj) {

		final ResultSet resultSet = context.mock(ResultSet.class);
		final ResultSetMetaData meta = context.mock(ResultSetMetaData.class);

		try {
			context.checking(new Expectations() {{
				oneOf(resultSet).getMetaData(); will(returnValue(meta));

				oneOf(meta).getColumnCount(); will(returnValue(cols.length));
				for (int i = 0; i < cols.length; ++i) {
					oneOf(meta).getColumnName(i + 1); will(returnValue(cols[i]));
				}

				for (int j = 0; j < obj.length; ++j) {
					oneOf(resultSet).next(); will(returnValue(true));
					for (int i = 0; i < obj[0].length; ++i) {
						oneOf(resultSet).getObject(i + 1); will(returnValue(obj[j][i]));
					}
				}

				between(0, 2).of(resultSet).next(); will(returnValue(false));
			}});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return resultSet;
	}

	public static Connection mkConnection(
			final Mockery context,
			final String table,
			final String[] cols,
			final Object[][] obj) {
		return mkConnection(context, table, null, cols, obj);
	}

	public static Connection mkConnection(
			final Mockery context,
			final String table,
			final String where,
			final String[] cols,
			final Object[][] obj) {

		final Connection connection = context.mock(Connection.class);
		final Statement statement = context.mock(Statement.class);
		final ResultSet resultSet = mkResultSet(context, cols, obj);

		final String sqlWhere;
		if (where != null && !where.equals("")) {
			sqlWhere = " WHERE " + where;
		} else {
			sqlWhere = "";
		}

		try {
			context.checking(new Expectations() {{
				oneOf(connection).createStatement(); will(returnValue(statement));
				oneOf(statement).executeQuery(
						"SELECT * FROM " + table + sqlWhere);
					will(returnValue(resultSet));
				oneOf(statement).close();
			}});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return connection;
	}
}
