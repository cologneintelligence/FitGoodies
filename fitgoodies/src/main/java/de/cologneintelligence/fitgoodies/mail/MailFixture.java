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

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.mail.providers.JavaMailMessageProvider;
import de.cologneintelligence.fitgoodies.mail.providers.MessageProvider;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Parse;
import fit.TypeAdapter;

import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fixture which checks the content of a mail. Before calling, a connection must
 * be set up using either the {@link SetupFixture} or the {@link SetupHelper}.
 * The fixture opens a connection, fetches the most recent mail, checks it,
 * deletes it by default and closes the connection again.
 * <p>
 * The table must have three columns. The first one contains the header name or
 * &quot;body&quot; to check the plain text and the HTML body or
 * &quot;htmlbody&quot;/&quot;plainbody&quot; to only check one of them.
 * <p>
 * The second column must contain the keywords &quot;contains&quot; or
 * &quot;regex&quot;, which decides how the third column is interpreted. The
 * third column contains the String which is compared with the selected content.
 * Cross References are supported in the third column only.
 * <p>
 * If a text matches, only the matching line is shown. If a regular expression
 * was used, the whole match is shown.
 * <p>
 * To not delete a mail after processing set the fixture parameter
 * &quot;delete&quot; to false.
 * <p>
 * Example:
 * <p>
 * <table border="1" summary="">
 * <tr><td>fitgoodies.mail.MailFixture</td><td>delete=false</td></tr>
 * <tr><td>body</td><td>contains</td><td>dear user</td></tr>
 * <tr><td>subject</td><td>regex</td><td>sp.m</td></tr>
 * </table>
 *
 */
public class MailFixture extends Fixture {
    private final MessageProvider provider;
    private Mail mail;

    /**
     * Generates a new fixture using the given provider.
     * @param provider message provider to use
     */
    public MailFixture(final MessageProvider provider) {
        this.provider = provider;
    }

    /**
     * Generates a new fixture using the standard provider (which is JavaMail).
     */
    public MailFixture() {
        this(new JavaMailMessageProvider(
                DependencyManager.getOrCreate(SetupHelper.class).generateProperties()));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        provider.connect();
        mail = provider.getLatestMessage();

        if (mail == null) {
            provider.disconnect();
            throw new RuntimeException("No mail found");
        }
    }

    @Override
    public void tearDown() throws Exception {
        if (FixtureTools.convertToBoolean(getParam("delete", "true"))) {
            mail.delete();
        }
        provider.disconnect();
        super.tearDown();
    }

    @Override
    public void doRow(final Parse row) {
        final String object = row.parts.text().toLowerCase();
        final String command = row.parts.more.text();
        final String content = parseContentCell(row);

        String[] objects;
        objects = getMailContent(object);

        boolean right = false;
        if (objects != null) {
            for (final String inspect : objects) {
                if (inspect != null) {
                    right = dispatchMatcher(row.parts, command, content, inspect);
                    if (right) {
                        break;
                    }
                }
            }
        }

        if (!right) {
            markError(row.parts.more.more, objects);
        }
    }

    private String parseContentCell(final Parse row) {
        try {
            final TypeAdapter ta = TypeAdapter.on(this, String.class);
            final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
            FixtureTools.processCell(row.parts.more.more, ta, this, helper);
            return row.parts.more.more.text();
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private void markError(final Parse cell, final String[] objects) {
        if (objects == null) {
            makeMoreString(cell, "(unset)", 0);
        } else {
            boolean found = false;
            for (final String o : objects) {
                if (o != null) {
                    found = true;
                    makeMoreString(cell, o, objects.length);
                    break;
                }
            }
            if (!found) {
                makeMoreString(cell, "(unset)", objects.length);
            }
        }
    }

    private void makeMoreString(final Parse cell, final String message, final int count) {
        wrong(cell, preview(message));
        if (count > 1) {
            info(cell, " (+ " + (count - 1) + " more)");
        }
    }

    private String preview(final String text) {
        final int PREVIEW_LENGTH = 128;
        String result = text;

        if (text.length() > PREVIEW_LENGTH) {
            result = text.substring(0, PREVIEW_LENGTH) + "...";
        }

        return result;
    }

    private boolean dispatchMatcher(final Parse cells, final String command,
            final String content, final String inspect) {
        if (command.equals("contains")) {
            return matchContains(cells.more.more, content, inspect);
        } else if (command.equals("regex")) {
            return matchRegex(cells.more.more, content, inspect);
        } else {
            ignore(cells.more);
            return true;
        }
    }

    private boolean matchContains(final Parse cell,
            final String expected, final String actual) {
        for (final String line : actual.split("\n")) {
            if (line.toLowerCase().contains(expected.toLowerCase())) {
                right(cell);
                cell.addToBody("<hr />" + escape(line));
                return true;
            }
        }
        return false;
    }

    private boolean matchRegex(final Parse cell,
            final String expected, final String actual) {
        final Matcher matcher = Pattern.compile(expected).matcher(actual);

        if (matcher.find()) {
            right(cell);
            cell.addToBody("<hr />" + escape(matcher.group(0)));
            return true;
        } else {
            return false;
        }
    }

    private String[] getMailContent(final String object) {
        try {
            if ("body".equals(object)) {
                return new String[]{mail.getPlainContent(), mail.getHTMLContent()};
            } else if ("plainbody".equals(object)) {
                return new String[]{mail.getPlainContent()};
            } else if ("htmlbody".equals(object)) {
                return new String[]{mail.getHTMLContent()};
            } else {
                return mail.getHeader(object);
            }
        } catch (final MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
