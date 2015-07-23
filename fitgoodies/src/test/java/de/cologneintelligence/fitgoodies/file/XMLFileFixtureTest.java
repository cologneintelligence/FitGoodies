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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class XMLFileFixtureTest extends FitGoodiesTestCase {
    private XMLFileFixture fixture;

    @Before
    public void setUp() throws Exception {
        final byte[] fileContent = ("<?xml version=\"1.0\"?>"
                + "<root><child1><child>Content</child><child>x</child></child1>"
                + "<sibling>Content 2</sibling>"
                + "</root>").getBytes("utf-16");

        //final FileInformation fileInfo = new FileInformation("/", "file.xml", fileContent);

        File directory = mock(File.class, "directory");
        File file = mock(File.class, "file");
        FileInformationWrapper wrapper = mock(FileInformationWrapper.class);
        FileInformation fileInformation = mock(FileInformation.class);

        when(directory.listFiles(argThat(is(any(FileFilter.class)))))
                .thenReturn(new File[]{file});
        when(wrapper.wrap(file)).thenReturn(fileInformation);
        when(fileInformation.openInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
        helper.setDirectory(directory);

        fixture = new XMLFileFixture(wrapper);
        fixture.setParams(new String[] {"pattern=.*", "encoding=utf-16"});
    }

    @Test
    public void testParsing() {
        final Parse table = parseTable(
                tr("/root/child1/child[1]", "Content"),
                tr("/root/child1/child[2]", "x"),
                tr("/root/sibling", "Content 1"));

        fixture.doTable(table);

        assertCounts(fixture.counts(), table, 2, 1, 0, 0);
    }

    @Test
    public void testParsingWithErrors() {
        final Parse table = parseTable(
                tr("/root/child1/child[1]"),
                tr("---", "x"));

        fixture.doTable(table);

        assertCounts(fixture.counts(), table, 0, 0, 0, 1);
    }

    @Test
    public void testParsingWithIgnores() {
        final Parse table = parseTable(
                        tr("/root/child1/child[1]", ""),
                        tr("/root/child1/child[2]", ""));

        fixture.doTable(table);

        assertCounts(fixture.counts(), table, 0, 0, 0, 0);
        assertThat(table.parts.more.parts.more.text(), is(equalTo("Content")));
        assertThat(table.parts.more.more.parts.more.text(), is(equalTo("x")));
    }
}
