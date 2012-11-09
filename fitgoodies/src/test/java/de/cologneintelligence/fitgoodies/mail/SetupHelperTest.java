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
import de.cologneintelligence.fitgoodies.util.DependencyManager;


/**
 * @author jwierum
 */
public final class SetupHelperTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.INSTANCE.getOrCreate(SetupHelper.class);
    }

    public void testErrors() {
        try {
            helper.generateProperties();
            fail("Expected error: no protocol set");
        } catch (RuntimeException e) {
            assertContains("protocol", e.getMessage());
        }

        helper.setProtocol("imap");
        try {
            helper.generateProperties();
            fail("Expected error: no inbox set");
        } catch (RuntimeException e) {
            assertContains("inbox", e.getMessage());
        }
    }

    public void testPOP3Setters1() {
        helper.setProtocol("pOp3");
        helper.setUsername("user");
        helper.setPassword("password");
        helper.setHost("127.0.0.1");
        helper.setInbox("test");
        helper.setPort(42);

        Properties p = helper.generateProperties();

        assertEquals("pop3", p.getProperty("mail.store.protocol"));
        assertEquals("127.0.0.1", p.getProperty("mail.pop3.host"));
        assertEquals("42", p.getProperty("mail.pop3.port"));
        assertEquals("user", p.getProperty("mail.username"));
        assertEquals("password", p.getProperty("mail.password"));
        assertEquals("INBOX", p.getProperty("mail.inbox"));
    }

    public void testPOP3Setters2() {
        helper.setProtocol("POP3");
        helper.setUsername("jw");
        helper.setPassword("secret");
        helper.setHost("mail.mycompany.xx");
        helper.setInbox("");

        Properties p = helper.generateProperties();

        assertEquals("pop3", p.getProperty("mail.store.protocol"));
        assertEquals("mail.mycompany.xx", p.getProperty("mail.pop3.host"));
        assertEquals("jw", p.getProperty("mail.username"));
        assertEquals("secret", p.getProperty("mail.password"));
        assertEquals("INBOX", p.getProperty("mail.inbox"));
        assertNull(p.getProperty("mail.pop3.port"));
    }

    public void testIMAPSetters1() {
        helper.setProtocol("imap");
        helper.setUsername("u");
        helper.setPassword("p");
        helper.setHost("localhost");
        helper.setInbox("inbx");
        helper.setPort(23);
        helper.setSSL(false);

        Properties p = helper.generateProperties();

        assertEquals("imap", p.getProperty("mail.store.protocol"));
        assertEquals("localhost", p.getProperty("mail.imap.host"));
        assertEquals("u", p.getProperty("mail.username"));
        assertEquals("p", p.getProperty("mail.password"));
        assertEquals("inbx", p.getProperty("mail.inbox"));
        assertEquals("23", p.getProperty("mail.imap.port"));
        assertNull(p.getProperty("mail.imap.ssl"));
    }

    public void testIMAPSetters2() {
        helper.setProtocol("IMAP");
        helper.setUsername("user");
        helper.setPassword("pass");
        helper.setHost("mail");
        helper.setInbox("home");
        helper.setSSL(true);

        Properties p = helper.generateProperties();

        assertEquals("imap", p.getProperty("mail.store.protocol"));
        assertEquals("mail", p.getProperty("mail.imap.host"));
        assertEquals("user", p.getProperty("mail.username"));
        assertEquals("pass", p.getProperty("mail.password"));
        assertEquals("home", p.getProperty("mail.inbox"));
        assertEquals("true", p.getProperty("mail.imap.ssl"));
    }

    public void testUnset() {
        helper.setProtocol("IMAP");
        helper.setUsername("user");
        helper.setHost("mail");
        helper.setInbox("home");

        Properties p = helper.generateProperties();

        assertNull(p.getProperty("mail.password"));
    }
}
