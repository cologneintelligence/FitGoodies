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

import de.cologneintelligence.fitgoodies.mail.providers.MessageProvider;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
	public void setup() throws MessagingException {
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
		useTable(
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

		expectValidationWithSuccess(0, "contains");
		expectValidationWithSuccess(1, "contains");
		expectValidationWithSuccess(2, "contains");
		expectValidationWithSuccess(3, "contains");
		expectValidationWithSuccess(4, "regex");

		run();

		assertThat(htmlAt(0, 2), containsAll("Text", "This is a simple TEXT"));
		assertThat(htmlAt(1, 2), containsAll("Simple", "A Simple test mail"));
		assertThat(htmlAt(2, 2), containsAll("server", "me@myserver.com"));
		assertThat(htmlAt(3, 2), containsAll("tld", "by gateway.tld now"));
		assertThat(htmlAt(4, 2), containsAll("\\d{3}", "423"));

		verifyCalls(mail, true);
	}

	@Test
	public void errorsAreReported() throws Exception {
		useTable(
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

		expectValidationWithFailure(0, "contains");
		expectValidationWithFailure(1, "is similar to");
		expectValidationWithFailure(2, "contains");
		expectValidationWithFailure(3, "regex");
		expectValidationWithFailure(4, "regex");
		expectValidationWithFailure(5, "regex");

		run();

        assertThat(htmlAt(0, 2), containsAll("some text", "expected",
            String.format("%s...", mailText.substring(0, PREVIEW_SIZE)),
            "actual", "(+ 1 more)"));
        assertThat(htmlAt(1, 2), containsAll("Simple", "expected", "A Simple test mail", "actual"));
        assertThat(htmlAt(2, 2), containsAll("empty?!", "expected", "(unset)", "actual"));
		assertThat(htmlAt(3, 2), containsAll("^\\d{3}$", "expected", "4235", "(+ 2 more)", "actual"));
		assertThat(htmlAt(4, 2), containsAll("x", "expected", "(unset)", "actual"));
		assertThat(htmlAt(5, 2), containsAll("7", "expected", "3", "(+ 5 more)", "actual"));
		assertThat(htmlAt(6, 2), containsAll("7", "expected", "(unset)", "actual"));

		verifyCalls(mail, true);
	}

	@Test
	public void testNoMail() throws Exception {
		useTable(tr("body", "contains", "some text"));

		reset(provider);
		run();
		assertCounts(0, 0, 0, 1);

		verifyCalls(null, false);
	}

	@Test
	public void testNoDelete() throws Exception {
		useTable();

		prepareParameterApply("delete", "evaluate to false", false);

        Map<String, String> params = new HashMap<>();
        params.put("delete", "evaluate to false");
        fixture.setParams(params);

		run();

		verifyCalls(mail, false);
	}

	@Test
	public void testPlainBody() throws Exception {
		useTable(
				tr("plainbody", "contains", "TEXT"),
				tr("plainbody", "contains", "different"));


		when(mail.getPlainContent()).thenReturn("Something different");

		expectValidationWithFailure(0, "contains");
		expectValidationWithFailure(1, "contains");

		run();

		assertThat(htmlAt(0, 2), containsAll("TEXT", "expected",
            "Something different", "actual"));
		assertThat(htmlAt(1, 2), containsAll("different", "Something different"));

		verifyCalls(mail, true);
	}

	@Test
	public void testHTMLBody() throws Exception {
		useTable(
				tr("htmlbody", "contains", "TEXT"),
				tr("htmlbody", "contains", "different"));


		when(mail.getHTMLContent()).thenReturn("Something different");

		expectValidationWithFailure(0, "contains");
		expectValidationWithFailure(1, "contains");

		run();

        assertThat(htmlAt(0, 2), containsAll("TEXT", "expected",
            "Something different", "actual"));
		assertThat(htmlAt(1, 2), containsAll("different",
				"Something different"));

		verifyCalls(mail, true);
	}

	protected void expectValidationWithFailure(final int row, final String expected) {
		final int col = 2;
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				cellAt(row, col).wrong("mocked Answer");
				return null;
			}
		}).when(validator).process(
				argThatSame(cellAt(row, col)),
            any(ConstantReceiver.class),
				argThat(is(equalTo(expected))),
				argThatSame(typeHandlerFactory));

		expectValidationWithSuccess(row, expected);
	}

	protected void expectValidationWithSuccess(final int row, final String expected) {
		expectations.add(new Task() {
			@Override
			public void run() throws Exception {
				verify(validator).process(
						argThatSame(cellAt(row, 2)),
                    any(ConstantReceiver.class),
						argThat(is(equalTo(expected))),
						argThatSame(typeHandlerFactory));
			}
		});
	}
}
