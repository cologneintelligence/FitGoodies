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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.mail.JavaMailMail;


/**
 * $Id$
 * @author jwierum
 */
public final class JavaMailMailTest extends FitGoodiesTestCase {
	{
		setImposteriser(ClassImposteriser.INSTANCE);
	}

	private Message message;
	private JavaMailMail mail;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		message = mock(Message.class);
		mail = new JavaMailMail(message);
	}

	public void testDelete() throws MessagingException {
		checking(new Expectations() {{
			oneOf(message).setFlag(Flag.DELETED, true);
		}});

		mail.delete();
	}

	public void testGetHeaders() throws MessagingException {
		checking(new Expectations() {{
			oneOf(message).getHeader("Received");
				will(returnValue(new String[]{"server1", "server2", "server3"}));
			oneOf(message).getHeader("X-My-Status");
				will(returnValue(new String[]{"test"}));
			oneOf(message).getHeader("X-Empty");
				will(returnValue(null));
		}});

		String[] actual;

		actual = mail.getHeader("Received");
		assertArray(actual, new String[]{"server1", "server2", "server3"});

		actual = mail.getHeader("X-My-Status");
		assertArray(actual, new String[]{"test"});

		actual = mail.getHeader("X-Empty");
		assertNull(actual);
	}

	public void testHastHTMLContentWithBaseMimeType() throws MessagingException {
		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(message).isMimeType("text/html"); will(returnValue(true));
		}});

		assertTrue(mail.hasHTMLContent());
	}

	public void testHastHTMLContentWithWrongMimeType() throws MessagingException {
		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(message).isMimeType("text/html"); will(returnValue(false));
		}});

		assertFalse(mail.hasHTMLContent());
	}

	public void testHasHTMLContentWithMultipleMimeTypes()
			throws MessagingException, IOException {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(true));
			oneOf(message).getContent(); will(returnValue(multipart));

			atLeast(1).of(multipart).getCount(); will(returnValue(2));
			oneOf(multipart).getBodyPart(0); will(returnValue(mimepart1));
			oneOf(multipart).getBodyPart(1); will(returnValue(mimepart2));

			oneOf(mimepart1).isMimeType("text/html"); will(returnValue(false));
			oneOf(mimepart2).isMimeType("text/html"); will(returnValue(true));

			oneOf(mimepart1).getDisposition(); will(returnValue(Part.INLINE.toLowerCase()));
			oneOf(mimepart2).getDisposition(); will(returnValue(Part.INLINE));
		}});

		assertTrue(mail.hasHTMLContent());
	}

	public void testHasHTMLContentWithMultipleMimeTypesNoMatches()
		throws MessagingException, IOException {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class);

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(true));
			oneOf(message).getContent(); will(returnValue(multipart));

			atLeast(1).of(multipart).getCount(); will(returnValue(1));
			oneOf(multipart).getBodyPart(0); will(returnValue(mimepart1));

			oneOf(mimepart1).isMimeType("text/html"); will(returnValue(false));

			oneOf(mimepart1).getDisposition(); will(returnValue(Part.INLINE));
		}});

		assertFalse(mail.hasHTMLContent());
	}

	public void testHasPlainContentWithMultipleMimeTypes()
			throws MessagingException, IOException {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(true));
			oneOf(message).getContent(); will(returnValue(multipart));

			atLeast(1).of(multipart).getCount(); will(returnValue(2));
			oneOf(multipart).getBodyPart(0); will(returnValue(mimepart1));

			oneOf(mimepart1).isMimeType("text/plain"); will(returnValue(true));

			oneOf(mimepart1).getDisposition(); will(returnValue(Part.INLINE.toUpperCase()));
		}});

		assertTrue(mail.hasPlainContent());
	}

	public void testHasPlainContentWithAttachments()
			throws MessagingException, IOException {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(true));
			oneOf(message).getContent(); will(returnValue(multipart));

			atLeast(1).of(multipart).getCount(); will(returnValue(2));
			oneOf(multipart).getBodyPart(0); will(returnValue(mimepart1));
			oneOf(multipart).getBodyPart(1); will(returnValue(mimepart2));

			oneOf(mimepart1).isMimeType("text/plain"); will(returnValue(false));

			oneOf(mimepart1).getDisposition(); will(returnValue(Part.INLINE.toUpperCase()));
			oneOf(mimepart2).getDisposition(); will(returnValue(Part.ATTACHMENT.toLowerCase()));
		}});

		assertFalse(mail.hasPlainContent());
	}

	public void testGetPlainContent()
			throws MessagingException, IOException {

		final String CONTENT = "Hello world";
		final InputStream content = new ByteArrayInputStream(CONTENT.getBytes("US-ASCII"));

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(message).isMimeType("text/plain"); will(returnValue(true));
			oneOf(message).getSize(); will(returnValue(CONTENT.length()));
			oneOf(message).getInputStream(); will(returnValue(content));
		}});

		assertEquals(CONTENT, mail.getPlainContent());
	}

	public void testGetHTMLContent()
			throws MessagingException, IOException {

		final String CONTENT = "Hello www world";
		final InputStream content = new ByteArrayInputStream(CONTENT.getBytes("US-ASCII"));

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(message).isMimeType("text/html"); will(returnValue(true));
			oneOf(message).getSize(); will(returnValue(CONTENT.length()));
			oneOf(message).getInputStream(); will(returnValue(content));
		}});

		assertEquals(CONTENT, mail.getHTMLContent());
	}

	public void testGetNonExistingPlainContent()
			throws MessagingException, IOException {

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(message).isMimeType("text/plain"); will(returnValue(false));
		}});

		assertNull(mail.getPlainContent());
	}

	public void testGetPlainContentWithEncoding()
			throws MessagingException, IOException {

		final String CONTENT = "Hello java world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("UTF-16");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

		final MimeMessage mimemessage = mock(MimeMessage.class);
		checking(new Expectations() {{
			oneOf(mimemessage).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(mimemessage).isMimeType("text/plain"); will(returnValue(true));
			oneOf(mimemessage).getEncoding(); will(returnValue("utf-16"));
			oneOf(mimemessage).getSize(); will(returnValue(CONTENTBYTES.length));
			oneOf(mimemessage).getInputStream(); will(returnValue(content));
		}});

		mail = new JavaMailMail(mimemessage);
		assertEquals(CONTENT, mail.getPlainContent());
	}

	public void testGetPlainContentWith7BitEncoding()
			throws MessagingException, IOException {

		final String CONTENT = "Hello mail world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("US-ASCII");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

		final MimeMessage mimemessage = mock(MimeMessage.class);
		checking(new Expectations() {{
			oneOf(mimemessage).isMimeType("multipart/*"); will(returnValue(false));
			oneOf(mimemessage).isMimeType("text/plain"); will(returnValue(true));
			oneOf(mimemessage).getEncoding(); will(returnValue("7bit"));
			oneOf(mimemessage).getSize(); will(returnValue(CONTENTBYTES.length));
			oneOf(mimemessage).getInputStream(); will(returnValue(content));
		}});

		mail = new JavaMailMail(mimemessage);
		assertEquals(CONTENT, mail.getPlainContent());
	}

	public void testGetPlainContentWithMultipart()
			throws MessagingException, IOException {

		final Multipart multipart = mock(Multipart.class);
		final MimeBodyPart mimepart1 = mock(MimeBodyPart.class, "mimepart1");
		final MimeBodyPart mimepart2 = mock(MimeBodyPart.class, "mimepart2");

		final String CONTENT = "Hello mime world";
		final byte[] CONTENTBYTES = CONTENT.getBytes("utf-16");
		final InputStream content = new ByteArrayInputStream(CONTENTBYTES);

		checking(new Expectations() {{
			oneOf(message).isMimeType("multipart/*"); will(returnValue(true));
			oneOf(message).getContent(); will(returnValue(multipart));

			atLeast(1).of(multipart).getCount(); will(returnValue(2));
			oneOf(multipart).getBodyPart(0); will(returnValue(mimepart1));
			oneOf(multipart).getBodyPart(1); will(returnValue(mimepart2));

			oneOf(mimepart2).isMimeType("text/plain"); will(returnValue(true));

			oneOf(mimepart1).getDisposition(); will(returnValue(Part.ATTACHMENT));
			oneOf(mimepart2).getDisposition(); will(returnValue(Part.INLINE));

			oneOf(mimepart2).getSize(); will(returnValue(CONTENTBYTES.length));
			oneOf(mimepart2).getEncoding(); will(returnValue("utf-16"));
			oneOf(mimepart2).getInputStream(); will(returnValue(content));
		}});

		assertEquals(CONTENT, mail.getPlainContent());
	}
}
