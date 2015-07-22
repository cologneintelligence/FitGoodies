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
import fit.Parse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class AbstractFilereaderFixtureTest extends FitGoodiesTestCase {
    public static class TestFixture extends AbstractFileReaderFixture {
        public String x;
    }

    private FileFixtureHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
    }

    @Test
    public void testDefaultParameters1() throws Exception {
        helper.setEncoding("latin-1");
        String pattern = ".*\\.bat";
        helper.setDirectory(mockDirectory(pattern, "f.txt.bat"));
        helper.setPattern(pattern);

        TestFixture fixture = new TestFixture();
        Parse table = parseTable(tr("x"));

        fixture.doTable(table);

        assertThat(fixture.getFile().toString(), is(equalTo("f.txt.bat")));
        assertThat(fixture.getEncoding(), is(equalTo("latin-1")));
    }

    @Test
    public void testDefaultParameters2() throws Exception {
        TestFixture fixture = new TestFixture();
        Parse table = parseTable(tr("x"));

        helper.setEncoding("utf-16");
        String pattern = ".*";
        helper.setDirectory(mockDirectory(pattern, "file1.txt"));
        helper.setPattern(pattern);

        fixture.doTable(table);

        assertThat(fixture.getFile().toString(), is(equalTo("file1.txt")));
        assertThat(fixture.getEncoding(), is(equalTo("utf-16")));
    }

    @Test
    public void testErrors() throws Exception {
        TestFixture fixture = new TestFixture();
        Parse table = parseTable(tr("x"));

        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
    }

    @Test
    public final void testCustomParameters() throws Exception {
        TestFixture fixture = new TestFixture();
        Parse table = parseTable(tr("x"));

        final String pattern = ".*\\.bat";
        helper.setDirectory(mockDirectory(pattern, "f.txt.bat"));
        fixture.setParams(new String[] {"pattern=" + pattern, "encoding=cp1252"});
        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 0)));

        assertThat(fixture.getFile().toString(), is(equalTo("f.txt.bat")));
        assertThat(fixture.getEncoding(), is(equalTo("cp1252")));
    }
}
