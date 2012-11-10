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


/**
 *
 */
package de.cologneintelligence.fitgoodies.file;

import java.io.FileNotFoundException;
import java.util.Iterator;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.DirectoryProvider;
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSelector;
import de.cologneintelligence.fitgoodies.file.FilenameNotUniqueException;
import de.cologneintelligence.fitgoodies.util.DependencyManager;


/**
 * @author jwierum
 */
public class FileSelectorTest extends FitGoodiesTestCase {
    public final void testFindFiles() throws FilenameNotUniqueException, FileNotFoundException {
        FileSelector fs = new FileSelector(new DirectoryProviderMock(), "file1\\.txt");

        assertEquals("file1.txt", fs.getUniqueFile().filename());
        fs = new FileSelector(new DirectoryProviderMock(), "file[26]\\.txt");

        assertEquals("file2.txt", fs.getUniqueFile().filename());
    }

    public final void testUniqueNameErrorHandling() throws FileNotFoundException {
        FileSelector fs = new FileSelector(new DirectoryProviderMock(), "file[23]\\.txt");

        try {
            fs.getUniqueFile().filename();
            fail("Non unique filename was not recognized");
        } catch (FilenameNotUniqueException e) {
        }

        fs = new FileSelector(new DirectoryProviderMock(), ".*");
        try {
            fs.getUniqueFile().filename();
            fail("Non unique filename was not recognized");
        } catch (FilenameNotUniqueException e) {
        }
    }

    public final void testNotFoundErrorHandling() throws FilenameNotUniqueException {
        FileSelector fs = new FileSelector(new DirectoryProviderMock(), "xyz");

        try {
            fs.getFirstFile();
            fail("FileNotFoundException error not raised");
        } catch (FileNotFoundException e) {
        }

        try {
            fs.getUniqueFile();
            fail("FileNotFound error not raised");
        } catch (FileNotFoundException e) {
        }

        fs = new FileSelector(new DirectoryProvider() {
            @Override public Iterator<DirectoryProvider> getDirectories() { return null; }
            @Override public String getPath() { return null; }

            @Override public Iterator<FileInformation> getFiles()
                    throws FileNotFoundException {
                throw new FileNotFoundException();
            }
        }, "");


        assertEquals(0, fs.getFiles().length);
    }


    public final void testGetLastFile() throws FileNotFoundException {
        FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
        helper.setEncoding("utf-8");
        helper.setPattern(".*");
        helper.setProvider(new DirectoryProviderMock());

        FileSelector fs = helper.getSelector();
        assertEquals("noext", fs.getLastFile().filename());
    }

    public final void testGetLastFileWithErrors() throws FileNotFoundException {
        FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
        helper.setEncoding("utf-8");
        helper.setPattern("nofile");
        helper.setProvider(new DirectoryProviderMock());

        FileSelector fs = helper.getSelector();
        try {
            fs.getLastFile();
            fail("Expected FileNotFoundException");
        } catch (FileNotFoundException e) {
        }
    }

    public final void testFirstFilename() throws FileNotFoundException {
        FileSelector fs = new FileSelector(new DirectoryProviderMock(), ".*");
        assertEquals("file1.txt", fs.getFirstFile().filename());
        assertEquals("file1.txt", fs.getFirstFile().filename());

        fs = new FileSelector(new DirectoryProviderMock(), ".*\\.bat");
        assertEquals("f.txt.bat", fs.getFirstFile().filename());
        assertEquals("f.txt.bat", fs.getFirstFile().filename());
    }

    public final void testFiles() throws FileNotFoundException {
        FileSelector fs = new FileSelector(new DirectoryProviderMock(), ".*\\.txt");

        FileInformation[] f = fs.getFiles();

        final int numberOfFiles = 3;
        assertEquals(numberOfFiles, f.length);
        assertEquals("file1.txt", f[0].filename());
        assertEquals("file3.txt", f[2].filename());
    }
}
