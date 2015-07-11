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

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Enables a user to setup a database connection from HTML.
 * <p
 *
 * Such a setup table could look like this (you need ojdbc*.jar in your
 * classpath):
 * <table border="1" summary="">
 * 		<tr><td>fitgoodies.database.SetupFixture</td></tr>
 * 		<tr><td>provider</td><td>oracle.jdbc.OracleDriver</td></tr>
 * 		<tr><td>connectionString</td><td>jdbc:oracle:thin:@Server:Port:Database</td></tr>
 * 		<tr><td>user</td><td>MY_USER</td></tr>
 * 		<tr><td>password</td><td>My_PaSsWoRd</td></tr>
 * </table>
 *
 */
public class SetupFixture extends ActionFixture {
    /**
     * Calls {@link #user(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void user() throws Exception {
        transformAndEnter();
    }

    /**
     * Sets the database username to {@code userName}.
     * The username can be received using {@link SetupHelper#getUser()}.
     * @param userName the database username
     */
    public void user(final String userName) {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setUser(userName);
    }

    /**
     * Calls {@link #password(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void password() throws Exception {
        transformAndEnter();
    }

    /**
     * Sets the database password to {@code password}.
     * The password can be received using {@link SetupHelper#getPassword()}.
     * @param password the database password
     */
    public void password(final String password) {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setPassword(password);
    }

    /**
     * Calls {@link #connectionString(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void connectionString() throws Exception {
        transformAndEnter();
    }

    /**
     * Sets the database connection string to {@code uri}.
     * The connection string can be received using
     * {@link SetupHelper#getConnectionString()}.
     * @param uri the database connectoin string
     */
    public void connectionString(final String uri) {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setConnectionString(uri);
    }

    /**
     * Calls {@link #provider(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void provider() throws Exception {
        transformAndEnter();
    }

    /**
     * Sets the database provider. The {@code providerName} must be a
     * fully qualified class name and the class must be in java's class path.
     * @param providerName fully qualified class name of a java.sql.Driver.
     * @throws Exception thrown if the class could not be found, initialized or
     * 		casted. Propagated to fit.
     */
    public void provider(final String providerName) throws Exception {
        SetupHelper.setProvider(providerName);
    }
}
