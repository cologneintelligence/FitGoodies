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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public final class SetupFixtureTest extends FitGoodiesFixtureTestCase<SetupFixture> {

    private SetupHelper helper;

    @Override
    protected Class<SetupFixture> getFixtureClass() {
        return SetupFixture.class;
    }

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(SetupHelper.class);
    }

    @Test
	public void testParses1() {
		useTable(
            tr("host", "$host"),
            tr("protocol", "pop3"),
            tr("username", "$user"),
            tr("password", "testpassword"),
            tr("ssl", "true"),
            tr("port", "123"));

        preparePreprocessWithConversion(String.class, "$host", "127.0.0.1");
        preparePreprocessWithConversion(String.class, "pop3", "pop3");
        preparePreprocessWithConversion(String.class, "$user", "testuser");
        preparePreprocessWithConversion(String.class, "testpassword", "testpassword");
        preparePreprocessWithConversion(String.class, "true", "true");
        preparePreprocessWithConversion(String.class, "123", "123");

		run();

        assertCounts(0, 0, 0, 0);
		Properties prop = helper.generateProperties();

		assertThat(prop.getProperty("mail.store.protocol"), is(equalTo("pop3")));
		assertThat(prop.getProperty("mail.pop3.host"), is(equalTo("127.0.0.1")));
		assertThat(prop.getProperty("mail.pop3.port"), is(equalTo("123")));
		assertThat(prop.getProperty("mail.pop3.ssl"), is(equalTo("true")));
		assertThat(prop.getProperty("mail.username"), is(equalTo("testuser")));
		assertThat(prop.getProperty("mail.password"), is(equalTo("testpassword")));
	}

	@Test
	public void testParses2() {
		useTable(
            tr("host", "localhost"),
            tr("protocol", "imap"),
            tr("username", "user"),
            tr("password", "passwd"),
            tr("inbox", "INBOX"));

        preparePreprocessWithConversion(String.class, "localhost", "localhost");
        preparePreprocessWithConversion(String.class, "imap", "imap");
        preparePreprocessWithConversion(String.class, "user", "user");
        preparePreprocessWithConversion(String.class, "passwd", "passwd");
        preparePreprocessWithConversion(String.class, "INBOX", "INBOX");

		run();

        assertCounts(0, 0, 0, 0);
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
