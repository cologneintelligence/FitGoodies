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

import java.io.File;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;


/**
 * Fixture which allows a user to select a file from its arguments.<br /><br />
 *
 * A fixture which extends the <code>AbstractFileReaderFixture</code> can
 * easily access a selected file via {@link #getFile()}. The selected encoding
 * can be retrieved via {@link #getEncoding()}.<br /><br />
 *
 * There are two ways of selecting a file. Either by providing a full file path,
 * or by using a pattern. In the latter case, the first matching file is selected:
 *
 * <table border="1"><tr>
 * <td>MyFixture</td><td>file=/path/to/file</td><td>encoding=latin-1</td>
 * </tr><tr><td colspan="3">...</td></tr></table><br />
 *
 * <table border="1"><tr>
 * <td>MyFixture</td><td>dir=/path/to/</td><td>pattern=.*\.txt</td>
 * </tr><tr><td colspan="3">...</td></tr></table><br />
 *
 * Either <code>file</code> or <code>pattern</code> must be provided.
 * <code>dir</code> and <code>encoding</code> are retrieved from the
 * {@link FileFixtureHelper} if they are missing.
 *
 * @author jwierum
 * @version $Id$
 */
public abstract class AbstractFileReaderFixture extends Fixture {
    private FileInformation file;
    private String encoding;

    /**
     * Reads the given parameters and initializes the values of
     * {@link #getEncoding()} and {@link #getFile()}.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        final CrossReferenceHelper crossReferenceHelper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        final FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
        encoding = helper.getEncoding();
        encoding = FixtureTools.getArg(args, "encoding", encoding,
                crossReferenceHelper);

        String fileName = FixtureTools.getArg(args, "file", null, crossReferenceHelper);
        if (fileName == null) {
            DirectoryProvider provider = helper.getProvider();

            final String dir = FixtureTools.getArg(args, "dir", null, crossReferenceHelper);
            if (dir != null) {
                provider = new FileSystemDirectoryProvider(dir);
            }

            if (provider == null) {
                throw new RuntimeException("No directory selected");
            }

            String pattern = helper.getPattern();
            pattern = FixtureTools.getArg(args, "pattern", pattern, crossReferenceHelper);

            final FileSelector fs = new FileSelector(provider, pattern);
            file = fs.getFirstFile();
        } else {
            String filePath = new File(fileName).getAbsolutePath();
            filePath = new File(filePath).getParent();
            fileName = new File(fileName).getName();
            file = new FileSystemFileInformation(filePath, fileName);
        }
    }

    /**
     * Gets the selected file.
     * @return the matching file
     */
    public final FileInformation getFile() {
        return file;
    }

    /**
     * Gets the selected encoding.
     * @return the encoding name
     */
    public final String getEncoding() {
        return encoding;
    }
}
