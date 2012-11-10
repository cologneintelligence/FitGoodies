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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePart;

/**
 * Implementation of {@link Mail} which processes JavaMail mails.
 *
 * @author jwierum
 * @version $Id$
 */
public class JavaMailMail implements Mail {
	private final Message message;

	/**
	 * Creates a new <code>JavaMailMail</code> object.
	 *
	 * @param innerMessage underlying message to process
	 */
	public JavaMailMail(final Message innerMessage) {
		message = innerMessage;
	}

	/**
	 * Returns all headers of the type <code>headerName</code>. If such a header
	 * does not exist, <code>null</code> should be returned.
	 *
	 * @param headerName header to read
	 * @return list of header values
	 * @throws MessagingException thrown by the underlying implementation
	 */
	@Override
	public String[] getHeader(final String headerName)
			throws MessagingException {
		return message.getHeader(headerName);
	}

	private boolean isMimeType(final String type)
			throws MessagingException {
		return message.isMimeType(type);
	}

	private boolean isMultipart()
			throws MessagingException {
		return isMimeType("multipart/*");
	}

	@Override
	public boolean hasHTMLContent() throws MessagingException {
		return hasMimeContent("text/html");
	}

	@Override
	public boolean hasPlainContent() throws MessagingException {
		return hasMimeContent("text/plain");
	}

	private boolean hasMimeContent(final String mimeType) throws MessagingException {
		if (isMultipart()) {
			Part part = getPart(mimeType);
			return part != null;
		} else {
			return isMimeType(mimeType);
		}
	}

	private MimePart getPart(final String mimeType) {
		try {
			Multipart multipart = (Multipart) message.getContent();

			for (int i = 0; i < multipart.getCount(); ++i) {
				BodyPart part = multipart.getBodyPart(i);
				if (isInline(part) && part.isMimeType(mimeType)) {
					return (MimeBodyPart) part;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	private boolean isInline(final Part part) throws MessagingException {
		String disposition = part.getDisposition();
		return disposition != null && disposition.equalsIgnoreCase(Part.INLINE);
	}

	@Override
	public String getHTMLContent() {
		return getMimeContent("text/html");
	}

	@Override
	public String getPlainContent() {
		return getMimeContent("text/plain");
	}

	private String getMimeContent(final String mimeType) {
		try {
			if (isMultipart()) {
				MimePart part = getPart(mimeType);
				if (part != null) {
					return readContent(part, part.getEncoding());
				}
			} else if (isMimeType(mimeType)) {
				String encoding = "US-ASCII";
				if (message instanceof MimePart) {
					encoding = ((MimePart) message).getEncoding();
				}
				return readContent(message, encoding);
			}
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException("Error while receiving mail", e);
		}

		return null;
	}

	private String readContent(final Part part, final String encoding)
			throws MessagingException, IOException {
		byte[] content = new byte[part.getSize()];

		String enc = encoding;
		if (encoding.equals("7bit")) {
			enc = "US-ASCII";
		}

		InputStream stream = part.getInputStream();
		stream.read(content);
		stream.close();

		return new String(content, Charset.forName(enc));
	}

	@Override
	public void delete() {
		try {
			message.setFlag(Flag.DELETED, true);
		} catch (MessagingException e) {
		}
	}
}
