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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class ResultSetMockGenerator {
    private final Connection connection;
    private final Statement statement;

    private String sqlClause;

    public ResultSetMockGenerator(
            final String table,
            final String[] cols,
            final Object[][] obj) {
        this(table, null, cols, obj);
    }

    public ResultSetMockGenerator(
            final String table,
            final String where,
            final String[] cols,
            final Object[][] obj) {

        try {
            connection = mock(Connection.class);
            statement = mock(Statement.class);
            final ResultSet resultSet;
            resultSet = mkResultSet(cols, obj);

            final String sqlWhere;
            if (where != null && !where.equals("")) {
                sqlWhere = " WHERE " + where;
            } else {
                sqlWhere = "";
            }
            sqlClause = "SELECT * FROM " + table + sqlWhere;

            when(connection.createStatement()).thenReturn(statement);
            when(statement.executeQuery(sqlClause)).thenReturn(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private ResultSet mkResultSet(final String[] cols, final Object[][] obj) throws SQLException {
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData meta = mock(ResultSetMetaData.class);

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
