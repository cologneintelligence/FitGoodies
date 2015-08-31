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

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.typehandler.BooleanTypeHandler;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * This fixture is used to define a connection to an e-mail provider.
 * The connection is not opened, this happens when the {@link MailFixture}
 * is used.
 * <p>
 * Setup example:
 * <table border="1" summary="">
 * <tr><td>fitgoodies.mail.SetupFixture</td></tr>
 * <tr><td>protocol</td><td>imap</td></tr>
 * <tr><td>host</td><td>imapserver.mycompany.com</td></tr>
 * <tr><td>port</td><td>993</td></tr>
 * <tr><td>ssl</td><td>true</td></tr>
 * <tr><td>username</td><td>user</td></tr>
 * <tr><td>password</td><td>pass</td></tr>
 * <tr><td>inbox</td><td>INBOX.Testfolder</td></tr>
 * </table>
 * <p>
 * Shorter example (no ssl, standard port):
 * <table border="1" summary="">
 * <tr><td>fitgoodies.mail.SetupFixture</td></tr>
 * <tr><td>protocol</td><td>pop3</td></tr>
 * <tr><td>host</td><td>pop3server.mycompany.com</td></tr>
 * <tr><td>username</td><td>user</td></tr>
 * <tr><td>password</td><td>pass</td></tr>
 * </table>
 */
public class SetupFixture extends ActionFixture {

	/**
	 * Calls {@link #host(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void host() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #protocol(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void protocol() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #username(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void username() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #password(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void password() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #inbox(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void inbox() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #ssl(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void ssl() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #port(String)}, using the next cell as its parameter.
	 *
	 * @throws Exception propagated to fit
	 */
	public void port() throws Exception {
		transformAndEnter();
	}

	/**
	 * Sets the SSL parameter of the mail {@link SetupHelper}.
	 *
	 * @param ssl indicates whether SSL shall be used
	 * @see SetupHelper#setSSL(boolean) SetupHelper.setSSL(boolean)
	 */
	public void ssl(final String ssl) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setSSL(BooleanTypeHandler.parseBool(ssl));
	}

	/**
	 * Sets the port parameter of the mail {@link SetupHelper}.
	 *
	 * @param port port to use
	 * @see SetupHelper#setPort(int) SetupHelper.setPort(int)
	 */
	public void port(final String port) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setPort(Integer.parseInt(port));
	}

	/**
	 * Sets the inbox parameter of the mail {@link SetupHelper}.
	 *
	 * @param inbox inbox to use
	 * @see SetupHelper#setInbox(String) SetupHelper.setInbox(String)
	 */
	public void inbox(final String inbox) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setInbox(inbox);
	}

	/**
	 * Sets the host parameter if the mail {@link SetupFixture}.
	 *
	 * @param host host to use
	 * @see SetupHelper#setHost(String) SetupHelper.setHost(String)
	 */
	public void host(final String host) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setHost(host);
	}

	/**
	 * Sets the user name parameter if the mail {@link SetupFixture}.
	 *
	 * @param username user name to use
	 * @see SetupHelper#setUsername(String) SetupHelper.setUsername(String)
	 */
	public void username(final String username) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setUsername(username);
	}

	/**
	 * Sets the password parameter if the mail {@link SetupFixture}.
	 *
	 * @param password password to use
	 * @see SetupHelper#setPassword(String) SetupHelper.setPassword(String)
	 */
	public void password(final String password) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setPassword(password);
	}

	/**
	 * Sets the protocol parameter if the mail {@link SetupFixture}.
	 *
	 * @param protocol protocol to use
	 * @see SetupHelper#setProtocol(String) SetupHelper.setProtocol(String)
	 */
	public void protocol(final String protocol) {
		DependencyManager.getOrCreate(SetupHelper.class)
				.setProtocol(protocol);
	}
}
