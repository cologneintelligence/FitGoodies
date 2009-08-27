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


package fitgoodies.mail.providers;

import javax.mail.MessagingException;

import fitgoodies.mail.Mail;

/**
 * Interface for a provider that can receive Mails.
 *
 * @author jwierum
 * @version $Id$
 */
public interface MessageProvider {
	/**
	 * Connects to the message server.
	 * @throws MessagingException thrown by the underlying connection
	 */
	void connect() throws MessagingException;

	/**
	 * Closes the connection.
	 * @throws MessagingException thrown by the underlying connection
	 */
	void disconnect() throws MessagingException;

	/**
	 * Gets the most recent mail. The Mail can not be processed (e.g. deleted
	 * when the connection is closed, so {@link #disconnect()} should be called
	 * <em>after</em> processing the mail.
	 *
	 * @return The latest message
	 * @throws MessagingException thrown by the underlying connection
	 */
	Mail getLatestMessage() throws MessagingException;
}