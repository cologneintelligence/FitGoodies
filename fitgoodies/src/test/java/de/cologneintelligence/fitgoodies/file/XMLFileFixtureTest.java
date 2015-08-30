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
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class XMLFileFixtureTest extends FitGoodiesFixtureTestCase<XMLFileFixture> {
    @Mock
    private FileInformationWrapper wrapper;

    @Override
    protected Class<XMLFileFixture> getFixtureClass() {
        return XMLFileFixture.class;
    }

    @Override
    protected XMLFileFixture newInstance() throws InstantiationException, IllegalAccessException {
        return new XMLFileFixture(wrapper);
    }

    @Before
	public void setUp() throws Exception {
		final byte[] fileContent = ("<?xml version=\"1.0\"?>"
				+ "<root><child1><child>Content</child><child>x</child></child1>"
				+ "<sibling>Content 2</sibling>"
				+ "</root>").getBytes("utf-16");

		File directory = mock(File.class, "directory");
		File file = mock(File.class, "file");
		FileInformation fileInformation = mock(FileInformation.class);

		when(directory.listFiles(argThat(is(any(FileFilter.class)))))
				.thenReturn(new File[]{file});
		when(wrapper.wrap(file)).thenReturn(fileInformation);
		when(fileInformation.openInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

		FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
		helper.setDirectory(directory);

        Map<String, String> params = new HashMap<>();
        params.put("pattern", "$pattern");
        params.put("encoding", "utf-16");
        fixture.setParams(params);

        expectParameterApply("pattern", "$pattern", ".*");
        expectParameterApply("encoding", "utf-16", "utf-16");
	}

	@Test
	public void testParsing() {
		useTable(
            tr("/root/child1/child[1]", "Content"),
            tr("/root/child1/child[2]", "x"),
            tr("/root/sibling", "Content 1"));

        expectConstantValidation(0, 1, "Content");
        expectConstantValidation(1, 1, "x");
        expectConstantValidation(2, 1, "Content 2");

		run();

		assertCounts(0, 0, 0, 0);
	}

	@Test
	public void testParsingWithErrors() {
		useTable(
				tr("/root/child1/child[1]"),
				tr("---", "x"));

		run();

		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void testParsingWithIgnores() {
		useTable(
				tr("/root/child1/child[1]", ""),
				tr("/root/child1/child[2]", ""));

        expectConstantValidation(0, 1, "Content");
        expectConstantValidation(1, 1, "x");

		run();
	}
}
