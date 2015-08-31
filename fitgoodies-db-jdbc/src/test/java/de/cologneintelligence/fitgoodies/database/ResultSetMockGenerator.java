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


import java.sql.*;

import static org.mockito.Mockito.*;

public class ResultSetMockGenerator {
	private Connection connection;
	private Statement statement;

    private String sqlClause;

	public ResultSetMockGenerator(
			String table,
			String[] cols,
			Object[][] obj) throws SQLException {
		this(table, null, cols, obj);
	}

	public ResultSetMockGenerator(
			String table,
			String where,
			String[] cols,
			Object[][] obj) throws SQLException {

        connection = mock(Connection.class);
        statement = mock(Statement.class);
        ResultSet resultSet = mkResultSet(cols, obj);

        String sqlWhere;
        if (where != null && !where.equals("")) {
            sqlWhere = " WHERE " + where;
        } else {
            sqlWhere = "";
        }
        sqlClause = "SELECT * FROM " + table + sqlWhere;

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(sqlClause)).thenReturn(resultSet);
	}


	private ResultSet mkResultSet(String[] cols, Object[][] obj) throws SQLException {
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(resultSet.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(cols.length);
		for (int i = 0; i < cols.length; ++i) {
			when(meta.getColumnName(i + 1)).thenReturn(cols[i]);
		}

		Boolean[] nextResult = new Boolean[obj.length];

		if (cols.length == 0) {
			when(resultSet.next()).thenReturn(false);
		} else {
			for (int j = 0; j < obj.length; j++) {
				nextResult[j] = j < obj.length - 1;

				for (int i = 0; i < obj[0].length; ++i) {
					when(resultSet.getObject(i + 1)).thenReturn(obj[j][i]);
				}
			}

			when(resultSet.next()).thenReturn(true, nextResult);
		}

		return resultSet;
	}

	public Connection getConnection() {
		return connection;
	}

	public void verifyInteractions() {
		try {
			verify(statement).executeQuery(sqlClause);
			verify(statement).close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
