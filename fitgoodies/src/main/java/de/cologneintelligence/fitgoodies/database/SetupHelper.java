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
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Helper class to manage database connection information.
 * To set the values with HTML, you can use a {@link SetupFixture}.
 *
 */
public final class SetupHelper {
    private String user;
    private String password;
    private String connectionString;

    /**
     * Sets the database driver. <code>driverName</code> must be a fully
     * qualified class name and the class must be in java's class path.
     * Unless the driver is already registered, <code>setProvider</code>
     * registers it at the <code>java.sql.DriverManager</code>.
     * @param driverName fully qualified class name of a <code>java.sql.Driver</code>.
     * @throws Exception thrown if the class could not be found, casted or
     * 		registered.
     */
    public static void setProvider(final String driverName) throws Exception {
        Driver driver = (Driver) Class.forName(driverName).newInstance();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            if (drivers.nextElement().getClass().equals(driver.getClass())) {
                return;
            }
        }

        DriverManager.registerDriver(driver);
    }

    /**
     * Sets the username to <code>userName</code>.
     * @param userName username to set
     * @see #getUser() getUser()
     */
    public void setUser(final String userName) {
        this.user = userName;
    }

    /**
     * Returns the selected username.
     * @return the username
     * @see #setUser(String) setUser(String)
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the selected password.
     * @return the password
     * @see #setPassword(String) setPassword(String)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password to <code>pw</code>.
     * @param pw the password to set
     * @see #getPassword() getPassword()
     */
    public void setPassword(final String pw) {
        this.password = pw;
    }

    /**
     * Returns the selected connection string.
     * The connection string format depends on the selected provider.
     * @return the selected connection string
     * @see #setConnectionString(String) setConnectionString(String)
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * Set the connection string to <code>connectString</code>.
     * The connection string format depends on the selected provider.
     * @param connectString the connection string to use
     * @see #getConnectionString() getConnectionString()
     */
    public void setConnectionString(final String connectString) {
        this.connectionString = connectString;
    }

    /**
     * Returns a <code>java.sql.Connection</code> using the saved connection
     * string, authenticating with the saved username and password.
     * @return an instance of java.sql.Connection
     * @throws SQLException thrown by the <code>DriverManager</code> indicating
     * 		some kind of problem.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString, user, password);
    }
}
