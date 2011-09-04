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


package de.cologneintelligence.fitgoodies.mail;

import java.util.Properties;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.mail.SetupHelper;


/**
 * $Id$
 * @author jwierum
 */
public final class SetupHelperTest extends FitGoodiesTestCase {
	public void testSingleton() {
		SetupHelper helper = SetupHelper.instance();
		assertNotNull(helper);

		assertSame(helper, SetupHelper.instance());

		SetupHelper.reset();
		assertNotSame(helper, SetupHelper.instance());
	}

	public void testErrors() {
		try {
			SetupHelper.instance().generateProperties();
			fail("Expected error: no protocol set");
		} catch (RuntimeException e) {
			assertContains("protocol", e.getMessage());
		}

		SetupHelper.instance().setProtocol("imap");
		try {
			SetupHelper.instance().generateProperties();
			fail("Expected error: no inbox set");
		} catch (RuntimeException e) {
			assertContains("inbox", e.getMessage());
		}
	}

	public void testPOP3Setters1() {
		SetupHelper.instance().setProtocol("pOp3");
		SetupHelper.instance().setUsername("user");
		SetupHelper.instance().setPassword("password");
		SetupHelper.instance().setHost("127.0.0.1");
		SetupHelper.instance().setInbox("test");
		SetupHelper.instance().setPort(42);

		Properties p = SetupHelper.instance().generateProperties();

		assertEquals("pop3", p.getProperty("mail.store.protocol"));
		assertEquals("127.0.0.1", p.getProperty("mail.pop3.host"));
		assertEquals("42", p.getProperty("mail.pop3.port"));
		assertEquals("user", p.getProperty("mail.username"));
		assertEquals("password", p.getProperty("mail.password"));
		assertEquals("INBOX", p.getProperty("mail.inbox"));
	}

	public void testPOP3Setters2() {
		SetupHelper.instance().setProtocol("POP3");
		SetupHelper.instance().setUsername("jw");
		SetupHelper.instance().setPassword("secret");
		SetupHelper.instance().setHost("mail.mycompany.xx");
		SetupHelper.instance().setInbox("");

		Properties p = SetupHelper.instance().generateProperties();

		assertEquals("pop3", p.getProperty("mail.store.protocol"));
		assertEquals("mail.mycompany.xx", p.getProperty("mail.pop3.host"));
		assertEquals("jw", p.getProperty("mail.username"));
		assertEquals("secret", p.getProperty("mail.password"));
		assertEquals("INBOX", p.getProperty("mail.inbox"));
		assertNull(p.getProperty("mail.pop3.port"));
	}

	public void testIMAPSetters1() {
		SetupHelper.instance().setProtocol("imap");
		SetupHelper.instance().setUsername("u");
		SetupHelper.instance().setPassword("p");
		SetupHelper.instance().setHost("localhost");
		SetupHelper.instance().setInbox("inbx");
		SetupHelper.instance().setPort(23);
		SetupHelper.instance().setSSL(false);

		Properties p = SetupHelper.instance().generateProperties();

		assertEquals("imap", p.getProperty("mail.store.protocol"));
		assertEquals("localhost", p.getProperty("mail.imap.host"));
		assertEquals("u", p.getProperty("mail.username"));
		assertEquals("p", p.getProperty("mail.password"));
		assertEquals("inbx", p.getProperty("mail.inbox"));
		assertEquals("23", p.getProperty("mail.imap.port"));
		assertNull(p.getProperty("mail.imap.ssl"));
	}

	public void testIMAPSetters2() {
		SetupHelper.instance().setProtocol("IMAP");
		SetupHelper.instance().setUsername("user");
		SetupHelper.instance().setPassword("pass");
		SetupHelper.instance().setHost("mail");
		SetupHelper.instance().setInbox("home");
		SetupHelper.instance().setSSL(true);

		Properties p = SetupHelper.instance().generateProperties();

		assertEquals("imap", p.getProperty("mail.store.protocol"));
		assertEquals("mail", p.getProperty("mail.imap.host"));
		assertEquals("user", p.getProperty("mail.username"));
		assertEquals("pass", p.getProperty("mail.password"));
		assertEquals("home", p.getProperty("mail.inbox"));
		assertEquals("true", p.getProperty("mail.imap.ssl"));
	}

	public void testUnset() {
		SetupHelper.instance().setProtocol("IMAP");
		SetupHelper.instance().setUsername("user");
		SetupHelper.instance().setHost("mail");
		SetupHelper.instance().setInbox("home");

		Properties p = SetupHelper.instance().generateProperties();

		assertNull(p.getProperty("mail.password"));
	}
}
