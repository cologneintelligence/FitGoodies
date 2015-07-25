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

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.mail.providers.MessageProvider;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Ignore;
import org.junit.Test;

import javax.mail.MessagingException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public final class MailFixtureTest extends FitGoodiesTestCase {
    private MessageProvider provider;

    private MailFixture prepareFixture(final Mail mail)
            throws Exception {
        provider = mock(MessageProvider.class);

        when(provider.getLatestMessage()).thenReturn(mail);

        return new MailFixture(provider);
    }

    public void verifyCalls(Mail mail, boolean expectDelete) {
        try {
            verify(provider).connect();
            verify(provider).getLatestMessage();
            verify(provider).disconnect();
            if (expectDelete) {
                verify(mail).delete();
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProcessing() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);

        Parse table = parseTable(
                tr("body", "contains", "Text"),
                tr("SUBJECT", "contains", "Simple"),
                tr("to", "contains", "server"),
                tr("received", "contains", "tld"),
                tr("date", "regex", "\\d{3}"));


        when(mail.getHTMLContent()).thenReturn("A mail!\nThis is a simple TEXT");
        when(mail.getPlainContent()).thenReturn("Another view");
        when(mail.getHeader("subject")).thenReturn(new String[]{null, "A Simple test mail", "uuh"});
        when(mail.getHeader("to")).thenReturn(new String[]{"me@myserver.com"});
        when(mail.getHeader("received")).thenReturn(new String[]{"by gateway.tld now"});
        when(mail.getHeader("date")).thenReturn(new String[]{"a423b"});

        fixture.doTable(table);

        assertThat(fixture.counts().right, is(equalTo((Object) 5)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

        assertThat(table.parts.more.parts.more.more.body, is(equalTo("Text<hr />This is a simple TEXT")));
        assertThat(table.parts.more.more.more.parts.more.more.body, is(equalTo("server<hr />me@myserver.com")));
        assertThat(table.parts.more.more.more.more.more.parts.more.more.body, is(equalTo("\\d{3}<hr />423")));

        verifyCalls(mail, true);
    }

    @Test
    public void testProcessingWithErrors() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);

        Parse table = parseTable(
                tr("body", "contains", "some text"),
                tr("SUBJECT", "is similar to", "Simple"),
                tr("to", "contains", "empty?!"),
                tr("date", "regex", "^\\d{3}$"),
                tr("custom", "regex", "x"),
                tr("X-MyHeader", "regex", "7"),
                tr("X-Null", "regex", "7"));


        final String mailText = "A mail!\nThis is a simple TEXT!"
                + "This text is longer than 128 characters**************"
                + "*****************************************************";
        final int PREVIEW_SIZE = 128;


        when(mail.getHTMLContent()).thenReturn("Another view");
        when(mail.getPlainContent()).thenReturn(mailText);
        when(mail.getHeader("subject")).thenReturn(new String[]{"A Simple test mail"});
        when(mail.getHeader("to")).thenReturn(new String[]{});
        when(mail.getHeader("date")).thenReturn(new String[]{"4235", "1234", "xzy?"});
        when(mail.getHeader("custom")).thenReturn(new String[]{null});
        when(mail.getHeader("x-myheader")).thenReturn(new String[]{null, null, "3", "2", "1", "4"});
        when(mail.getHeader("x-null")).thenReturn(null);

        fixture.doTable(table);

        assertThat(fixture.counts().wrong, is(equalTo((Object) 6)));
        assertThat(fixture.counts().right, is(equalTo((Object) 0)));
        assertThat(fixture.counts().ignores, is(equalTo((Object) 1)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

        assertThat(table.parts.more.parts.more.more.text(), is(equalTo("some text expected"
                + mailText.substring(0, PREVIEW_SIZE)
                + "... actual (+ 1 more)")));
        assertThat(table.parts.more.more.more.parts.more.more.text(), is(equalTo("empty?! expected(unset) actual")));
        assertThat(table.parts.more.more.more.more.parts.more.more.text(), is(equalTo("^\\d{3}$ expected4235 actual (+ 2 more)")));
        assertThat(table.parts.more.more.more.more.more.parts.more.more.text(), is(equalTo("x expected(unset) actual")));
        assertThat(table.parts.more.more.more.more.more.more.parts.more.more.text(), is(equalTo("7 expected3 actual (+ 5 more)")));
        assertThat(table.parts.more.more.more.more.more.more.more.parts.more.more.text(), is(equalTo("7 expected(unset) actual")));

        verifyCalls(mail, true);
    }

    @Test
    public void testNoMail() throws Exception {
        MailFixture fixture = prepareFixture(null);
        Parse table = parseTable(tr("body", "contains", "some text"));

        fixture.doTable(table);
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));

        verifyCalls(null, false);
    }

    @Test
    public void testNoDelete() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);
        Parse table = parseTable();

        fixture.setParams(new String[]{"delete=no"});
        fixture.doTable(table);

        verifyCalls(mail, false);
    }

    @Test
    public void testPlainBody() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);

        Parse table = parseTable(
                tr("plainbody", "contains", "TEXT"),
                tr("plainbody", "contains", "different"));


        when(mail.getPlainContent()).thenReturn("Something different");

        fixture.doTable(table);

        assertThat(fixture.counts().right, is(equalTo((Object) 1)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 1)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

        assertThat(table.parts.more.parts.more.more.text(), is(equalTo("TEXT expectedSomething different actual")));
        assertThat(table.parts.more.more.parts.more.more.body, is(equalTo("different<hr />Something different")));

        verifyCalls(mail, true);
    }

    @Test
    public void testHTMLBody() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);

        Parse table = parseTable(
                tr("htmlbody", "contains", "TEXT"),
                tr("htmlbody", "contains", "different"));


        when(mail.getHTMLContent()).thenReturn("Something different");

        fixture.doTable(table);

        assertThat(fixture.counts().right, is(equalTo((Object) 1)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 1)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

        assertThat(table.parts.more.parts.more.more.text(), is(equalTo("TEXT expectedSomething different actual")));
        assertThat(table.parts.more.more.parts.more.more.body, is(equalTo("different<hr />Something different")));

        verifyCalls(mail, true);
    }

    @Test
    @Ignore
    public void testCRF() throws Exception {
        final Mail mail = mock(Mail.class);
        MailFixture fixture = prepareFixture(mail);

        CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        helper.parseBody("${tests.put(body)}", "this goes to the body");
        Parse table = parseTable(
                tr("plainbody", "contains", "${tests.get(body)}"));

        when(mail.getPlainContent()).thenReturn("this goes to the body");

        fixture.doTable(table);
        assertThat(fixture.counts().right, is(equalTo((Object) 1)));

        verifyCalls(mail, true);
    }
}
