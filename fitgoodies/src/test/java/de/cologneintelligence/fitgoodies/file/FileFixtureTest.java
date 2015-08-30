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

package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FileFixtureTest extends FitGoodiesFixtureTestCase<FileFixture> {
    private FileFixtureHelper helper;

    @Override
    protected Class<FileFixture> getFixtureClass() {
        return FileFixture.class;
    }

    @Before
    public void setUp() {
        this.helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
    }

	@Test
	public void testErrors() {
		useTable(
            tr("too short"),
            tr("wrong", "value"));

        run();

        assertCounts(0, 0, 0, 2);
	}

	@Test
	public void testPattern1() {
        useTable(tr("pattern", "$pattern"));

        preparePreprocessWithConversion(String.class, "$pattern", ".*\\.txt");

        run();

        assertCounts(0, 0, 0, 0);

		assertThat(helper.getPattern(), is(equalTo(".*\\.txt")));
    }

    @Test
    public void testPattern2() {
		useTable(tr("pattern", "testfile"));

        preparePreprocessWithConversion(String.class, "testfile", "testfile");

        run();

        assertCounts(0, 0, 0, 0);

		assertThat(helper.getPattern(), is(equalTo("testfile")));
	}

	@Test
	public void testEncoding1() {
        useTable(
            tr("directory", "$dir"),
            tr("encoding", "utf-8"));

        preparePreprocessWithConversion(String.class, "$dir", "dir");
        preparePreprocessWithConversion(String.class, "utf-8", "utf-8");

        run();

        assertCounts(0, 0, 0, 0);
        assertThat(helper.getEncoding(), is(equalTo("utf-8")));
    }

    @Test
    public void testEncoding2() {
		useTable(
				tr("directory", "c:\\"),
				tr("encoding", "latin-1"));

        preparePreprocessWithConversion(String.class, "c:\\", "c:\\");
        preparePreprocessWithConversion(String.class, "latin-1", "latin-1");

        run();

        assertCounts(0, 0, 0, 0);
		assertThat(helper.getEncoding(), is(equalTo("latin-1")));
	}

}
