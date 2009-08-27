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

import org.jmock.Expectations;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.mail.providers.MessageProvider;

/**
 * $Id: MailFixtureTest.java 195 2009-08-21 09:58:17Z jwierum $
 * @author jwierum
 */
public final class MailFixtureTest extends FitGoodiesTestCase {
	private MailFixture prepareFixture(final Mail mail, final boolean expectDelete)
			throws Exception {
		final MessageProvider provider = mock(MessageProvider.class);

		checking(new Expectations() {{
			oneOf(provider).connect();
			oneOf(provider).getLatestMessage(); will(returnValue(mail));
			oneOf(provider).disconnect();

			if (expectDelete) {
				oneOf(mail).delete();
			}
		}});

		MailFixture fixture = new MailFixture(provider);

		return fixture;
	}

	public void testProcessing() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, true);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>body</td><td>contains</td><td>Text</td></tr>"
				+ "<tr><td>SUBJECT</td><td>contains</td><td>Simple</td></tr>"
				+ "<tr><td>to</td><td>contains</td><td>server</td></tr>"
				+ "<tr><td>received</td><td>contains</td><td>tld</td></tr>"
				+ "<tr><td>date</td><td>regex</td><td>\\d{3}</td></tr>"
				+ "</table>"
				);


		checking(new Expectations() {{
			oneOf(mail).getHTMLContent();
				will(returnValue("A mail!\nThis is a simple TEXT"));
			oneOf(mail).getPlainContent();
				will(returnValue("Another view"));
			oneOf(mail).getHeader("subject");
				will(returnValue(new String[]{null, "A Simple test mail", "uuh"}));
			oneOf(mail).getHeader("to");
				will(returnValue(new String[]{"me@myserver.com"}));
			oneOf(mail).getHeader("received");
				will(returnValue(new String[]{"by gateway.tld now"}));
			oneOf(mail).getHeader("date");
				will(returnValue(new String[]{"a423b"}));
		}});

		fixture.doTable(table);

		assertEquals(5, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);

		assertEquals("Text<hr />This is a simple TEXT",
				table.parts.more.parts.more.more.body);
		assertEquals("server<hr />me@myserver.com",
				table.parts.more.more.more.parts.more.more.body);
		assertEquals("\\d{3}<hr />423",
				table.parts.more.more.more.more.more.parts.more.more.body);
	}

	public void testProcessingWithErrors() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, true);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>body</td><td>contains</td><td>some text</td></tr>"
				+ "<tr><td>SUBJECT</td><td>is similar to</td><td>Simple</td></tr>"
				+ "<tr><td>to</td><td>contains</td><td>empty?!</td></tr>"
				+ "<tr><td>date</td><td>regex</td><td>^\\d{3}$</td></tr>"
				+ "<tr><td>custom</td><td>regex</td><td>x</td></tr>"
				+ "<tr><td>X-MyHeader</td><td>regex</td><td>7</td></tr>"
				+ "<tr><td>X-Null</td><td>regex</td><td>7</td></tr>"
				+ "</table>"
				);


		final String mailText = "A mail!\nThis is a simple TEXT!"
			+ "This text is longer than 128 characters**************"
			+ "*****************************************************";
		final int PREVIEW_SIZE = 128;


		checking(new Expectations() {{
			oneOf(mail).getHTMLContent();
				will(returnValue("Another view"));
			oneOf(mail).getPlainContent();
				will(returnValue(mailText));
			oneOf(mail).getHeader("subject");
				will(returnValue(new String[]{"A Simple test mail"}));
			oneOf(mail).getHeader("to");
				will(returnValue(new String[]{}));
			oneOf(mail).getHeader("date");
				will(returnValue(new String[]{"4235", "1234", "xzy?"}));
			oneOf(mail).getHeader("custom");
				will(returnValue(new String[]{null}));
			oneOf(mail).getHeader("x-myheader");
				will(returnValue(new String[]{null, null, "3", "2", "1", "4"}));
			oneOf(mail).getHeader("x-null");
				will(returnValue(null));
		}});

		fixture.doTable(table);

		assertEquals(6, fixture.counts.wrong);
		assertEquals(0, fixture.counts.right);
		assertEquals(1, fixture.counts.ignores);
		assertEquals(0, fixture.counts.exceptions);

		assertEquals("some text expected"
				+ mailText.substring(0, PREVIEW_SIZE)
				+ "... actual (+ 1 more)",
				table.parts.more.parts.more.more.text());
		assertEquals("empty?! expected(unset) actual",
				table.parts.more.more.more.parts.more.more.text());
		assertEquals("^\\d{3}$ expected4235 actual (+ 2 more)",
				table.parts.more.more.more.more.parts.more.more.text());
		assertEquals("x expected(unset) actual",
				table.parts.more.more.more.more.more.parts.more.more.text());
		assertEquals("7 expected3 actual (+ 5 more)",
				table.parts.more.more.more.more.more.more.parts.more.more.text());
		assertEquals("7 expected(unset) actual",
				table.parts.more.more.more.more.more.more.more.parts.more.more.text());
	}

	public void testNoMail() throws Exception {
		MailFixture fixture = prepareFixture(null, false);
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>body</td><td>contains</td><td>some text</td></tr>"
				+ "</table>"
				);

		fixture.doTable(table);
		assertEquals(1, fixture.counts.exceptions);
	}

	public void testNoDelete() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, false);
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "</table>"
				);

		fixture.setParams(new String[]{"delete=no"});
		fixture.doTable(table);
	}

	public void testPlainBody() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, true);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>plainbody</td><td>contains</td><td>TEXT</td></tr>"
				+ "<tr><td>plainbody</td><td>contains</td><td>different</td></tr>"
				+ "</table>"
				);


		checking(new Expectations() {{
			atLeast(1).of(mail).getPlainContent();
				will(returnValue("Something different"));
		}});

		fixture.doTable(table);

		assertEquals(1, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);

		assertEquals("TEXT expectedSomething different actual",
				table.parts.more.parts.more.more.text());
		assertEquals("different<hr />Something different",
				table.parts.more.more.parts.more.more.body);
	}

	public void testHTMLBody() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, true);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>htmlbody</td><td>contains</td><td>TEXT</td></tr>"
				+ "<tr><td>htmlbody</td><td>contains</td><td>different</td></tr>"
				+ "</table>"
				);


		checking(new Expectations() {{
			atLeast(1).of(mail).getHTMLContent();
				will(returnValue("Something different"));
		}});

		fixture.doTable(table);

		assertEquals(1, fixture.counts.right);
		assertEquals(1, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);

		assertEquals("TEXT expectedSomething different actual",
				table.parts.more.parts.more.more.text());
		assertEquals("different<hr />Something different",
				table.parts.more.more.parts.more.more.body);
	}

	public void testCRF() throws Exception {
		final Mail mail = mock(Mail.class);
		MailFixture fixture = prepareFixture(mail, true);

		fitgoodies.references.CrossReferenceHelper.instance().parseBody(
				"${tests.put(body)}", "this goes to the body");
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>plainbody</td><td>contains</td><td>${tests.get(body)}</td></tr>"
				+ "</table>");

		checking(new Expectations() {{
			atLeast(1).of(mail).getPlainContent();
				will(returnValue("this goes to the body"));
		}});

		fixture.doTable(table);
		assertEquals(1, fixture.counts.right);
	}
}
