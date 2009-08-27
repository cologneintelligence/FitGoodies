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


package fitgoodies.database;

import java.sql.Connection;
import java.sql.DriverManager;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public class SetupHelperTest extends FitGoodiesTestCase {
	public final void testSingleton() {
		SetupHelper expected = SetupHelper.instance();
		assertNotNull(expected);
		assertSame(expected, SetupHelper.instance());
		SetupHelper.reset();
		assertNotSame(expected, SetupHelper.instance());
	}

	public final void testProvider() throws Exception {
		SetupHelper.setProvider("fitgoodies.database.DriverMock");
		assertNotNull(DriverManager.getDriver("jdbc://test"));
	}

	public final void testUser() {
		SetupHelper.instance().setUser("test");
		assertEquals("test", SetupHelper.instance().getUser());
		SetupHelper.instance().setUser("user");
		assertEquals("user", SetupHelper.instance().getUser());
	}

	public final void testPassword() {
		SetupHelper.instance().setPassword("pass");
		assertEquals("pass", SetupHelper.instance().getPassword());
		SetupHelper.instance().setPassword("pw2");
		assertEquals("pw2", SetupHelper.instance().getPassword());
	}

	public final void testSetConnectionString() {
		SetupHelper.instance().setConnectionString("text");
		assertEquals("text", SetupHelper.instance().getConnectionString());
		SetupHelper.instance().setConnectionString("t2");
		assertEquals("t2", SetupHelper.instance().getConnectionString());
	}

	public final void testGetConnection() throws Exception {
		SetupHelper.setProvider("fitgoodies.database.DriverMock");
		SetupHelper.instance().setUser("username");
		SetupHelper.instance().setPassword("pw1");
		SetupHelper.instance().setConnectionString("jdbc://test/url");
		Connection conn = SetupHelper.instance().getConnection();
		assertNotNull(conn);
		assertEquals(DriverMock.getLastReturnedConnection(), conn);
	}
}
