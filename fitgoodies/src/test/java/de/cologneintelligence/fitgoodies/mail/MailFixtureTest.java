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

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.mail.providers.MessageProvider;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;


public final class MailFixtureTest extends FitGoodiesFixtureTestCase<MailFixture> {
	@Mock
	Mail mail;

	@Mock
	MessageProvider provider;

	@Override
	protected Class<MailFixture> getFixtureClass() {
		return MailFixture.class;
	}

	@Override
	protected MailFixture newInstance() throws InstantiationException, IllegalAccessException {
		return new MailFixture(provider);
	}

	@Before
	public void prepareFixture() throws MessagingException {
		when(provider.getLatestMessage()).thenReturn(mail);
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
		Parse table = parseTable(
				tr("body", "contains", "Text"),
				tr("SUBJECT", "contains", "Simple"),
				tr("to", "contains", "server"),
				tr("received", "contains", "tld"),
				tr("date", "regex", "\\d{3}"));


		when(mail.getHTMLContent()).thenReturn("Another view");
		when(mail.getPlainContent()).thenReturn("A mail!\nThis is a simple TEXT");
		when(mail.getHeader("subject")).thenReturn(new String[]{null, "A Simple test mail", "uuh"});
		when(mail.getHeader("to")).thenReturn(new String[]{"me@myserver.com"});
		when(mail.getHeader("received")).thenReturn(new String[]{"by gateway.tld now"});
		when(mail.getHeader("date")).thenReturn(new String[]{"a423b"});

		expectValidationWithSuccess(table, 1, "contains");
		expectValidationWithSuccess(table, 2, "contains");
		expectValidationWithSuccess(table, 3, "contains");
		expectValidationWithSuccess(table, 4, "contains");
		expectValidationWithSuccess(table, 5, "regex");


		fixture.doTable(table);

		assertThat(table.at(0, 1, 2).body, containsAll("Text", "<hr />", "This is a simple TEXT"));
		assertThat(table.at(0, 2, 2).body, containsAll("Simple", "<hr />", "A Simple test mail"));
		assertThat(table.at(0, 3, 2).body, containsAll("server", "<hr />", "me@myserver.com"));
		assertThat(table.at(0, 4, 2).body, containsAll("tld", "<hr />", "by gateway.tld now"));
		assertThat(table.at(0, 5, 2).body, containsAll("\\d{3}", "<hr />", "423"));

		verifyCalls(mail, true);
	}

	@Test
	public void errorsAreReported() throws Exception {
		Parse table = parseTable(
				tr("body", "contains", "some text"),
				tr("SUBJECT", "is similar to", "Simple"),
				tr("to", "contains", "empty?!"),
				tr("date", "regex", "^\\d{3}$"),
				tr("custom", "regex", "x"),
				tr("X-MyHeader", "regex", "7"),
				tr("X-Null", "regex", "7"));


		final String mailText = "A mail! This is a simple TEXT!"
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

		expectValidationWithFailure(table, 1, "contains");
		expectValidationWithFailure(table, 2, "is similar to");
		expectValidationWithFailure(table, 3, "contains");
		expectValidationWithFailure(table, 4, "regex");
		expectValidationWithFailure(table, 5, "regex");
		expectValidationWithFailure(table, 6, "regex");

		fixture.doTable(table);

		assertThat(table.at(0, 1, 2).text(), is(equalTo(String.format(
				"some text expected%s... (+ 1 more) actual", mailText.substring(0, PREVIEW_SIZE)))));
		assertThat(table.at(0, 3, 2).text(), is(equalTo("empty?! expected(unset) actual")));
		assertThat(table.at(0, 4, 2).text(), is(equalTo("^\\d{3}$ expected4235 (+ 2 more) actual")));
		assertThat(table.at(0, 5, 2).text(), is(equalTo("x expected(unset) actual")));
		assertThat(table.at(0, 6, 2).text(), is(equalTo("7 expected3 (+ 5 more) actual")));
		assertThat(table.at(0, 7, 2).text(), is(equalTo("7 expected(unset) actual")));

		verifyCalls(mail, true);
	}

	@Test
	public void testNoMail() throws Exception {
		Parse table = parseTable(tr("body", "contains", "some text"));

		reset(provider);
		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 1);

		verifyCalls(null, false);
	}

	@Test
	public void testNoDelete() throws Exception {
		Parse table = parseTable();

		expectParameterApply("delete", "no", false);

		fixture.setParams(new String[]{"delete=no"});
		fixture.doTable(table);

		verifyCalls(mail, false);
	}

	@Test
	public void testPlainBody() throws Exception {
		final Parse table = parseTable(
				tr("plainbody", "contains", "TEXT"),
				tr("plainbody", "contains", "different"));


		when(mail.getPlainContent()).thenReturn("Something different");

		expectValidationWithFailure(table, 1, "contains");
		expectValidationWithFailure(table, 2, "contains");

		fixture.doTable(table);

		assertThat(table.at(0, 1, 2).text(), is(equalTo("TEXT expectedSomething different actual")));
		assertThat(table.at(0, 2, 2).body, containsAll("different", "<hr />", "Something different"));

		verifyCalls(mail, true);
	}

	@Test
	public void testHTMLBody() throws Exception {
		Parse table = parseTable(
				tr("htmlbody", "contains", "TEXT"),
				tr("htmlbody", "contains", "different"));


		when(mail.getHTMLContent()).thenReturn("Something different");

		expectValidationWithFailure(table, 1, "contains");
		expectValidationWithFailure(table, 2, "contains");

		fixture.doTable(table);

		assertThat(table.at(0, 1, 2).text(), is(equalTo("TEXT expectedSomething different actual")));
		assertThat(table.at(0, 2, 2).body, containsAll("different", "<hr />",
				"Something different"));

		verifyCalls(mail, true);
	}

	protected void expectValidationWithFailure(final Parse table, final int row, final String expected) {
		final int col = 2;
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				FitUtils.wrong(table.at(0, row, col), "mocked Answer");
				return null;
			}
		}).when(validator).process(
				argThatSame(table.at(0, row, col)),
				argThatSame(fixture.counts()),
				any(ConstantReceiver.class),
				argThat(is(equalTo(expected))),
				argThatSame(typeHandlerFactory));

		expectValidationWithSuccess(table, row, expected);
	}

	protected void expectValidationWithSuccess(final Parse table, final int row, final String expected) {
		expectations.add(new Task() {
			@Override
			public void run() throws Exception {
				verify(validator).process(
						argThatSame(table.at(0, row, 2)),
						argThatSame(fixture.counts()),
						any(ConstantReceiver.class),
						argThat(is(equalTo(expected))),
						argThatSame(typeHandlerFactory));
			}
		});
	}

	private Matcher<String> containsAll(String... values) {
		List<Matcher<? super String>> matchers = new ArrayList<>(values.length);
		for (String string : values) {
			matchers.add(containsString(string));
		}

		return allOf(matchers);
	}
}
