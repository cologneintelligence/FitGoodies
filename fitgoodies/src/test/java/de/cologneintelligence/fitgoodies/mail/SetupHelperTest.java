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

package de.cologneintelligence.fitgoodies.mail;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public final class SetupHelperTest extends FitGoodiesTestCase {
	private SetupHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = DependencyManager.getOrCreate(SetupHelper.class);
	}

	@Test
	public void testErrors() {
		try {
			helper.generateProperties();
			Assert.fail("Expected error: no protocol set");
		} catch (RuntimeException e) {
			assertThat(e.getMessage(), containsString("protocol"));
		}

		helper.setProtocol("imap");
		try {
			helper.generateProperties();
			Assert.fail("Expected error: no inbox set");
		} catch (RuntimeException e) {
			assertThat(e.getMessage(), containsString("inbox"));
		}
	}

	@Test
	public void testPOP3Setters1() {
		helper.setProtocol("pOp3");
		helper.setUsername("user");
		helper.setPassword("password");
		helper.setHost("127.0.0.1");
		helper.setInbox("test");
		helper.setPort(42);

		Properties p = helper.generateProperties();

		assertThat(p.getProperty("mail.store.protocol"), is(equalTo("pop3")));
		assertThat(p.getProperty("mail.pop3.host"), is(equalTo("127.0.0.1")));
		assertThat(p.getProperty("mail.pop3.port"), is(equalTo("42")));
		assertThat(p.getProperty("mail.username"), is(equalTo("user")));
		assertThat(p.getProperty("mail.password"), is(equalTo("password")));
		assertThat(p.getProperty("mail.inbox"), is(equalTo("INBOX")));
	}

	@Test
	public void testPOP3Setters2() {
		helper.setProtocol("POP3");
		helper.setUsername("jw");
		helper.setPassword("secret");
		helper.setHost("mail.mycompany.xx");
		helper.setInbox("");

		Properties p = helper.generateProperties();

		assertThat(p.getProperty("mail.store.protocol"), is(equalTo("pop3")));
		assertThat(p.getProperty("mail.pop3.host"), is(equalTo("mail.mycompany.xx")));
		assertThat(p.getProperty("mail.username"), is(equalTo("jw")));
		assertThat(p.getProperty("mail.password"), is(equalTo("secret")));
		assertThat(p.getProperty("mail.inbox"), is(equalTo("INBOX")));
		assertThat(p.getProperty("mail.pop3.port"), is(nullValue()));
	}

	@Test
	public void testIMAPSetters1() {
		helper.setProtocol("imap");
		helper.setUsername("u");
		helper.setPassword("p");
		helper.setHost("localhost");
		helper.setInbox("inbx");
		helper.setPort(23);
		helper.setSSL(false);

		Properties p = helper.generateProperties();

		assertThat(p.getProperty("mail.store.protocol"), is(equalTo("imap")));
		assertThat(p.getProperty("mail.imap.host"), is(equalTo("localhost")));
		assertThat(p.getProperty("mail.username"), is(equalTo("u")));
		assertThat(p.getProperty("mail.password"), is(equalTo("p")));
		assertThat(p.getProperty("mail.inbox"), is(equalTo("inbx")));
		assertThat(p.getProperty("mail.imap.port"), is(equalTo("23")));
		assertThat(p.getProperty("mail.imap.ssl"), is(nullValue()));
	}

	@Test
	public void testIMAPSetters2() {
		helper.setProtocol("IMAP");
		helper.setUsername("user");
		helper.setPassword("pass");
		helper.setHost("mail");
		helper.setInbox("home");
		helper.setSSL(true);

		Properties p = helper.generateProperties();

		assertThat(p.getProperty("mail.store.protocol"), is(equalTo("imap")));
		assertThat(p.getProperty("mail.imap.host"), is(equalTo("mail")));
		assertThat(p.getProperty("mail.username"), is(equalTo("user")));
		assertThat(p.getProperty("mail.password"), is(equalTo("pass")));
		assertThat(p.getProperty("mail.inbox"), is(equalTo("home")));
		assertThat(p.getProperty("mail.imap.ssl"), is(equalTo("true")));
	}

	@Test
	public void testUnset() {
		helper.setProtocol("IMAP");
		helper.setUsername("user");
		helper.setHost("mail");
		helper.setInbox("home");

		Properties p = helper.generateProperties();

		assertThat(p.getProperty("mail.password"), is(nullValue()));
	}
}
