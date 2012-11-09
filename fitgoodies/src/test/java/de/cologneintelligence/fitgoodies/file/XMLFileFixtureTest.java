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


package de.cologneintelligence.fitgoodies.file;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Iterator;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.DirectoryProvider;
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileIterator;
import de.cologneintelligence.fitgoodies.file.XMLFileFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import fit.Parse;

/**
 * $Id$
 * @author jwierum
 */
public class XMLFileFixtureTest extends FitGoodiesTestCase {
    private XMLFileFixture fixture;

    @Override
    public final void setUp() throws Exception {
        super.setUp();

        final FileInformationMock fileInfo = new FileInformationMock("/", "file.xml",
                ("<?xml version=\"1.0\"?>"
                        + "<root><child1><child>Content</child><child>x</child></child1>"
                        + "<sibling>Content 2</sibling>"
                        + "</root>").getBytes("utf-16"));

        FileFixtureHelper helper = DependencyManager.INSTANCE.getOrCreate(FileFixtureHelper.class);
        helper.setProvider(new DirectoryProvider() {
            @Override public final Iterator<DirectoryProvider> getDirectories()
                    throws FileNotFoundException { return null; }

            @Override public final Iterator<FileInformation> getFiles()
                    throws FileNotFoundException {
                return new FileIterator(new FileInformation[]{fileInfo});
            }

            @Override public final String getPath() { return null; }
        });


        fixture = new XMLFileFixture();
        fixture.setParams(new String[] {"pattern=.*", "encoding=utf-16"});
    }

    public final void testParsing() throws ParseException {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>/root/child1/child[1]</td><td>Content</td></tr>"
                        + "<tr><td>/root/child1/child[2]</td><td>x</td></tr>"
                        + "<tr><td>/root/sibling</td><td>Content 1</td></tr>"
                        + "</table>");

        fixture.doTable(table);

        assertEquals(2, fixture.counts.right);
        assertEquals(1, fixture.counts.wrong);
        assertEquals(0, fixture.counts.exceptions);
        assertEquals(0, fixture.counts.ignores);
    }

    public final void testParsingWithErrors() throws ParseException {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>/root/child1/child[1]</td></tr>"
                        + "<tr><td>---</td><td>x</td></tr>"
                        + "</table>");

        fixture.doTable(table);

        assertEquals(0, fixture.counts.right);
        assertEquals(0, fixture.counts.wrong);
        assertEquals(1, fixture.counts.exceptions);
        assertEquals(0, fixture.counts.ignores);
    }

    public final void testParsingWithIgnores() throws ParseException {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>/root/child1/child[1]</td><td></td></tr>"
                        + "<tr><td>/root/child1/child[2]</td><td></td></tr>"
                        + "</table>");

        fixture.doTable(table);

        assertEquals(0, fixture.counts.right);
        assertEquals(0, fixture.counts.wrong);
        assertEquals(0, fixture.counts.exceptions);
        assertEquals("Content", table.parts.more.parts.more.text());
        assertEquals("x", table.parts.more.more.parts.more.text());
    }
}
