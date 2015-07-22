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


package de.cologneintelligence.fitgoodies.mail;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public final class SetupFixtureTest extends FitGoodiesTestCase {
    @Test
    public void testPares1() {
        Parse table1 = parseTable(
                tr("host", "127.0.0.1"),
                tr("protocol", "pop3"),
                tr("username", "testuser"),
                tr("password", "testpassword"),
                tr("ssl", "true"),
                tr("port", "123"));

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table1);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        Properties prop = helper.generateProperties();

        assertThat(prop.getProperty("mail.store.protocol"), is(equalTo("pop3")));
        assertThat(prop.getProperty("mail.pop3.host"), is(equalTo("127.0.0.1")));
        assertThat(prop.getProperty("mail.pop3.port"), is(equalTo("123")));
        assertThat(prop.getProperty("mail.pop3.ssl"), is(equalTo("true")));
        assertThat(prop.getProperty("mail.username"), is(equalTo("testuser")));
        assertThat(prop.getProperty("mail.password"), is(equalTo("testpassword")));
    }

    @Test
    public void testPares2() {
        Parse table1 = parseTable(
                tr("host", "localhost"),
                tr("protocol", "imap"),
                tr("username", "user"),
                tr("password", "passwd"),
                tr("inbox", "INBOX"));

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table1);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        Properties prop = helper.generateProperties();

        assertThat(prop.getProperty("mail.inbox"), is(equalTo("INBOX")));
        assertThat(prop.getProperty("mail.store.protocol"), is(equalTo("imap")));
        assertThat(prop.getProperty("mail.imap.host"), is(equalTo("localhost")));
        assertThat(prop.getProperty("mail.imap.port"), is(nullValue()));
        assertThat(prop.getProperty("mail.imap.ssl"), is(nullValue()));
        assertThat(prop.getProperty("mail.username"), is(equalTo("user")));
        assertThat(prop.getProperty("mail.password"), is(equalTo("passwd")));
    }
}
