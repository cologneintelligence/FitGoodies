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

import java.util.Properties;

/**
 * Singleton class that holds information on how to access mails.
 * Can be set via HTML using the {@link SetupFixture}.
 *
 * @author jwierum
 */
public final class SetupHelper {
    private String user;
    private String pass;
    private String proto;
    private String host;
    private String inbox;
    private boolean ssl;
    private int port;

    private void setProperty(final Properties prop,
            final String name, final String value) {
        if (value != null) {
            prop.setProperty(name, value);
        }
    }

    /**
     * Generates a properties object which can be used by
     * {@link de.cologneintelligence.fitgoodies.mail.providers.JavaMailMessageProvider}.
     * Default values are not set.
     *
     * @return properties object
     */
    public Properties generateProperties() {
        Properties result = new Properties();

        if (proto == null) {
            throw new RuntimeException("no protocol selected");
        }

        String protocol = proto.toLowerCase();

        setProperty(result, "mail.store.protocol", protocol);
        setProperty(result, "mail." + protocol + ".host", host);
        setProperty(result, "mail.username", user);
        setProperty(result, "mail.password", pass);

        if (port != 0) {
            setProperty(result, "mail." + protocol + ".port", Integer.toString(port));
        }

        if (ssl) {
            setProperty(result, "mail." + protocol + ".ssl", "true");
        }

        if (protocol.equals("pop3")) {
            setProperty(result, "mail.inbox", "INBOX");
        } else {
            if (inbox == null) {
                throw new RuntimeException("no inbox selected");
            }

            setProperty(result, "mail.inbox", inbox);
        }

        return result;
    }

    /**
     * Sets the protocol to use.
     * @param protocol protocol to use
     */
    public void setProtocol(final String protocol) {
        proto = protocol;
    }

    /**
     * Sets the user name to use.
     * @param username user name to use
     */
    public void setUsername(final String username) {
        user = username;
    }

    /**
     * Sets the password to use.
     * @param password the password to use
     */
    public void setPassword(final String password) {
        pass = password;
    }

    /**
     * Sets the hostname to use.
     * @param hostname hostname to use
     */
    public void setHost(final String hostname) {
        host = hostname;
    }

    /**
     * Sets the inbox to use.
     * @param inboxname inbox to use
     */
    public void setInbox(final String inboxname) {
        inbox = inboxname;
    }

    /**
     * Sets whether SSL will be used.
     * @param enable sets the SSL state
     */
    public void setSSL(final boolean enable) {
        ssl = enable;
    }

    /**
     * Sets the port that will be used.
     * @param port port to use
     */
    public void setPort(final int port) {
        this.port = port;
    }
}
