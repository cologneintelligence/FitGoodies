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
import org.junit.Before;
import org.junit.Test;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public final class JavaMailMailTest extends FitGoodiesTestCase {
	private Message message;
	private JavaMailMail mail;

	@Before
	public void setUp() throws Exception {
		message = mock(Message.class);
		mail = new JavaMailMail(message);
	}

	@Test
	public void testDelete() throws MessagingException {
		mail.delete();
		verify(message).setFlag(Flag.DELETED, true);
	}

	@Test
	public void testGetHeaders() throws MessagingException {
		when(message.getHeader("Received"))
				.thenReturn(new String[]{"server1", "server2", "server3"});
		when(message.getHeader("X-My-Status")).thenReturn(new String[]{"test"});
		when(message.getHeader("X-Empty")).thenReturn(null);

		String[] actual;

		actual = mail.getHeader("Received");
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList("server1", "server2", "server3"))));

		actual = mail.getHeader("X-My-Status");
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList("test"))));

		actual = mail.getHeader("X-Empty");
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void testHastHTMLContentWithBaseMimeType() throws MessagingException {
		when(message.isMimeType("multipart/*")).thenReturn(false);
		when(message.isMimeType("text/html")).thenReturn(true);

		assertThat(mail.hasHTMLContent(), is(true));
	}

	@Test
	public void testHastHTMLContentWithWrongMimeType() throws MessagingException {
		when(message.isMimeType("multipart/*")).thenReturn(false);
		when(message.isMimeType("text/html")).thenReturn(false);

		assertThat(mail.hasHTMLContent(), is(false));

	}

	@Test
	public void testHasHTMLContentWithMultipleMimeTypes() throws Exception {
		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		when(message.isMimeType("multipart/*")).thenReturn(true);
		when(message.getContent()).thenReturn(multipart);

		when(multipart.getCount()).thenReturn(2);
		when(multipart.getBodyPart(0)).thenReturn(mimepart1);
		when(multipart.getBodyPart(1)).thenReturn(mimepart2);

		when(mimepart1.isMimeType("text/html")).thenReturn(false);
		when(mimepart2.isMimeType("text/html")).thenReturn(true);

		when(mimepart1.getDisposition()).thenReturn(Part.INLINE.toLowerCase());
		when(mimepart2.getDisposition()).thenReturn(Part.INLINE);

		assertThat(mail.hasHTMLContent(), is(true));
	}

	@Test
	public void testHasHTMLContentWithMultipleMimeTypesNoMatches() throws Exception {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class);

			when(message.isMimeType("multipart/*")).thenReturn(true);
			when(message.getContent()).thenReturn(multipart);

			when(multipart.getCount()).thenReturn(1);
			when(multipart.getBodyPart(0)).thenReturn(mimepart1);

			when(mimepart1.isMimeType("text/html")).thenReturn(false);

			when(mimepart1.getDisposition()).thenReturn(Part.INLINE);

		assertThat(mail.hasHTMLContent(), is(false));

	}

	@Test
	public void testHasPlainContentWithMultipleMimeTypes() throws Exception {
		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");

		when(message.isMimeType("multipart/*")).thenReturn(true);
		when(message.getContent()).thenReturn(multipart);

		when(multipart.getCount()).thenReturn(2);
		when(multipart.getBodyPart(0)).thenReturn(mimepart1);

		when(mimepart1.isMimeType("text/plain")).thenReturn(true);
		when(mimepart1.getDisposition()).thenReturn(Part.INLINE.toUpperCase());

		assertThat(mail.hasPlainContent(), is(true));
	}

	@Test
	public void testHasPlainContentWithAttachments() throws Exception {
		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		when(message.isMimeType("multipart/*")).thenReturn(true);
		when(message.getContent()).thenReturn(multipart);

		when(multipart.getCount()).thenReturn(2);
		when(multipart.getBodyPart(0)).thenReturn(mimepart1);
		when(multipart.getBodyPart(1)).thenReturn(mimepart2);

		when(mimepart1.isMimeType("text/plain")).thenReturn(false);

		when(mimepart1.getDisposition()).thenReturn(Part.INLINE.toUpperCase());
		when(mimepart2.getDisposition()).thenReturn(Part.ATTACHMENT.toLowerCase());

		assertThat(mail.hasPlainContent(), is(false));

	}

	@Test
	public void testGetPlainContent() throws Exception {
		final String CONTENT = "Hello world";
		final InputStream content = new ByteArrayInputStream(CONTENT.getBytes("US-ASCII"));

		when(message.isMimeType("multipart/*")).thenReturn(false);
		when(message.isMimeType("text/plain")).thenReturn(true);
		when(message.getSize()).thenReturn(CONTENT.length());
		when(message.getInputStream()).thenReturn(content);

		assertThat(mail.getPlainContent(), is(equalTo(CONTENT)));
	}

	@Test
	public void testGetHTMLContent() throws Exception {
		final String CONTENT = "Hello www world";
		final InputStream content = new ByteArrayInputStream(CONTENT.getBytes("US-ASCII"));

		when(message.isMimeType("multipart/*")).thenReturn(false);
		when(message.isMimeType("text/html")).thenReturn(true);
		when(message.getSize()).thenReturn(CONTENT.length());
		when(message.getInputStream()).thenReturn(content);

		assertThat(mail.getHTMLContent(), is(equalTo(CONTENT)));
	}

	@Test
	public void testGetNonExistingPlainContent() throws Exception {

		when(message.isMimeType("multipart/*")).thenReturn(false);
		when(message.isMimeType("text/plain")).thenReturn(false);

		assertThat(mail.getPlainContent(), is(nullValue()));
	}

	@Test
	public void testGetPlainContentWithEncoding() throws Exception {

		final String CONTENT = "Hello java world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("UTF-16");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

		final MimeMessage mimemessage = mock(MimeMessage.class);
		when(mimemessage.isMimeType("multipart/*")).thenReturn(false);
		when(mimemessage.isMimeType("text/plain")).thenReturn(true);
		when(mimemessage.getEncoding()).thenReturn("utf-16");
		when(mimemessage.getSize()).thenReturn(CONTENTBYTES.length);
		when(mimemessage.getInputStream()).thenReturn(content);

		mail = new JavaMailMail(mimemessage);
		assertThat(mail.getPlainContent(), is(equalTo(CONTENT)));
	}

	@Test
	public void testGetPlainContentWith7BitEncoding() throws Exception {
		final String CONTENT = "Hello mail world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("US-ASCII");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

		final MimeMessage mimemessage = mock(MimeMessage.class);
		when(mimemessage.isMimeType("multipart/*")).thenReturn(false);
		when(mimemessage.isMimeType("text/plain")).thenReturn(true);
		when(mimemessage.getEncoding()).thenReturn("7bit");
		when(mimemessage.getSize()).thenReturn(CONTENTBYTES.length);
		when(mimemessage.getInputStream()).thenReturn(content);

		mail = new JavaMailMail(mimemessage);
		assertThat(mail.getPlainContent(), is(equalTo(CONTENT)));
	}

	@Test
	public void testGetPlainContentWithMultipart() throws Exception {
		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		final String CONTENT = "Hello mime world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("utf-16");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

			when(message.isMimeType("multipart/*")).thenReturn(true);
			when(message.getContent()).thenReturn(multipart);

			when(multipart.getCount()).thenReturn(2);
			when(multipart.getBodyPart(0)).thenReturn(mimepart1);
			when(multipart.getBodyPart(1)).thenReturn(mimepart2);

			when(mimepart1.getDisposition()).thenReturn(Part.ATTACHMENT);

			when(mimepart2.isMimeType("text/plain")).thenReturn(true);
			when(mimepart2.getDisposition()).thenReturn(Part.INLINE);
			when(mimepart2.getSize()).thenReturn(CONTENTBYTES.length);
			when(mimepart2.getEncoding()).thenReturn("utf-16");
			when(mimepart2.getInputStream()).thenReturn(content);

		assertThat(mail.getPlainContent(), is(equalTo(CONTENT)));
	}
}
