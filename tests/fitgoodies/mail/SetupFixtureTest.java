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


package fitgoodies.mail;

import java.text.ParseException;
import java.util.Properties;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public final class SetupFixtureTest extends FitGoodiesTestCase {
	public void testPares1() throws ParseException {
		Parse table1 = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>host</td><td>127.0.0.1</td></tr>"
				+ "<tr><td>protocol</td><td>pop3</td></tr>"
				+ "<tr><td>username</td><td>testuser</td></tr>"
				+ "<tr><td>password</td><td>testpassword</td></tr>"
				+ "<tr><td>ssl</td><td>true</td></tr>"
				+ "<tr><td>port</td><td>123</td></tr>"
				+ "</table>");

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table1);

		assertEquals(0, fixture.counts.exceptions);
		Properties prop = SetupHelper.instance().generateProperties();

		assertEquals("pop3", prop.getProperty("mail.store.protocol"));
		assertEquals("127.0.0.1", prop.getProperty("mail.pop3.host"));
		assertEquals("123", prop.getProperty("mail.pop3.port"));
		assertEquals("true", prop.getProperty("mail.pop3.ssl"));
		assertEquals("testuser", prop.getProperty("mail.username"));
		assertEquals("testpassword", prop.getProperty("mail.password"));
	}

	public void testPares2() throws ParseException {
		Parse table1 = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>host</td><td>localhost</td></tr>"
				+ "<tr><td>protocol</td><td>imap</td></tr>"
				+ "<tr><td>username</td><td>user</td></tr>"
				+ "<tr><td>password</td><td>passwd</td></tr>"
				+ "<tr><td>inbox</td><td>INBOX</td></tr>"
				+ "</table>");

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table1);

		assertEquals(0, fixture.counts.exceptions);
		Properties prop = SetupHelper.instance().generateProperties();

		assertEquals("INBOX", prop.getProperty("mail.inbox"));
		assertEquals("imap", prop.getProperty("mail.store.protocol"));
		assertEquals("localhost", prop.getProperty("mail.imap.host"));
		assertNull(prop.getProperty("mail.imap.port"));
		assertNull(prop.getProperty("mail.imap.ssl"));
		assertEquals("user", prop.getProperty("mail.username"));
		assertEquals("passwd", prop.getProperty("mail.password"));
	}
}
