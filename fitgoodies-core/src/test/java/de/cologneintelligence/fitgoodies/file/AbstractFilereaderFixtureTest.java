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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public class AbstractFilereaderFixtureTest extends FitGoodiesFixtureTestCase<AbstractFilereaderFixtureTest.TestFixture> {
	public static class TestFixture extends AbstractFileReaderFixture {
		public String x;
	}

	private FileFixtureHelper helper;

    @Override
    protected Class<TestFixture> getFixtureClass() {
        return TestFixture.class;
    }

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

		useTable(tr("x"));

		run();

		assertThat(fixture.getFile().toString(), is(equalTo("f.txt.bat")));
		assertThat(fixture.getEncoding(), is(equalTo("latin-1")));
	}

	@Test
	public void testDefaultParameters2() throws Exception {
		useTable(tr("x"));

		helper.setEncoding("utf-16");
		String pattern = ".*";
		helper.setDirectory(mockDirectory(pattern, "file1.txt"));
		helper.setPattern(pattern);

		run();

		assertThat(fixture.getFile().toString(), is(equalTo("file1.txt")));
		assertThat(fixture.getEncoding(), is(equalTo("utf-16")));
	}

	@Test
	public void testErrors() throws Exception {
		useTable(tr("x"));

		run();

        assertCounts(0, 0, 0, 1);
	}

	@Test
	public final void testCustomParameters() throws Exception {
		useTable(tr("x"));

		final String pattern = ".*\\.bat";
		helper.setDirectory(mockDirectory(pattern, "f.txt.bat"));

        Map<String, String> params = new HashMap<>();
        params.put("pattern", "p1");
        params.put("encoding", "p2");
        fixture.setParams(params);

        preparePreprocess("p1", pattern);
        prepareNonExistingArg("pattern");
        prepareParameterApply("encoding", "p2", "cp1252");

		run();

        assertCounts(0, 0, 1, 0);

		assertThat(fixture.getFile().toString(), is(equalTo("f.txt.bat")));
		assertThat(fixture.getEncoding(), is(nullValue()));
	}
}
