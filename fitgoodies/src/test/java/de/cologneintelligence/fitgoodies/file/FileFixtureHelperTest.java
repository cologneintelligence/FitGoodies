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

import java.io.FileNotFoundException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.file.FileSelector;


/**
 * @author jwierum
 */
public class FileFixtureHelperTest extends FitGoodiesTestCase {
    private FileFixtureHelper helper;

    @Override
    public void setUp() throws Exception {
        helper = new FileFixtureHelper();
    }

    public final void testSelector() throws FileNotFoundException {
        helper.setEncoding("utf-8");
        helper.setPattern(".*");
        helper.setProvider(new DirectoryProviderMock());

        FileSelector fs = helper.getSelector();
        assertEquals("file1.txt", fs.getFirstFile().filename());

        fs = helper.getSelector();
        assertEquals("file1.txt", fs.getFirstFile().filename());

        helper.setPattern(".*\\.bat");
        fs = helper.getSelector();
        assertEquals("f.txt.bat", fs.getFirstFile().filename());

        fs = helper.getSelector();
        assertEquals("f.txt.bat", fs.getFirstFile().filename());
    }

    public final void testEncoding() {
        helper.setEncoding("utf-8");
        assertEquals("utf-8", helper.getEncoding());

        helper.setEncoding("latin-1");
        assertEquals("latin-1", helper.getEncoding());
    }

    public final void testPattern() {
        helper.setPattern("*\\.txt");
        assertEquals("*\\.txt", helper.getPattern());

        helper.setPattern("*\\.bat");
        assertEquals("*\\.bat", helper.getPattern());
    }

    public final void testDirectory() {
        helper.setProvider(new DirectoryProviderMock());
        assertEquals("/test", helper.getProvider().getPath());
    }
}
