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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;

public class DriverMock implements Driver {
    private static Connection lastReturnedConnection;

    public static void setLastReturnedConnection(final Connection connection) {
        lastReturnedConnection = connection;
    }

    public DriverMock() {
        Connection connection = mock(Connection.class);
        setLastReturnedConnection(connection);
    }

    @Override
    public final boolean acceptsURL(final String url) throws SQLException {
        return url.startsWith("jdbc://test");
    }

    @Override
    public final Connection connect(final String url, final Properties info) throws SQLException {
        if (!url.startsWith("jdbc://test")) {
            return null;
        }

        if (!info.get("user").toString().startsWith("user")) {
            return null;
        }

        if (!info.get("password").toString().startsWith("pw")) {
            return null;
        }

        return lastReturnedConnection;
    }

    @Override
    public final int getMajorVersion() {
        return 1;
    }

    @Override
    public final int getMinorVersion() {
        return 0;
    }

    @Override
    public final DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info)
            throws SQLException {
        return null;
    }

    @Override
    public final boolean jdbcCompliant() {
        return true;
    }

    public static Connection getLastReturnedConnection() {
        return lastReturnedConnection;
    }

    public static void cleanup() {
        try {
            DriverManager.deregisterDriver(DriverManager.getDriver("jdbc://test"));
        } catch (SQLException e) {
        }
    }

    // This method must not have an '@Override' Annotation,
    // because the method does not exist in Java 7
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
