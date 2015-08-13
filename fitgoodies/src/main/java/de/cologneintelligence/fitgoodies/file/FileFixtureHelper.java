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

/**
 * Helper class to provide a possibility to select files.
 *
 * @see FileFixture FileFixture
 */
public class FileFixtureHelper {
    private String encoding;
    private String pattern;
    private File directory;

    /**
     * Sets the encoding.
     * @param fileEncoding the encoding to set
     * @see #getEncoding() getEncoding()
     */
    public void setEncoding(final String fileEncoding) {
        this.encoding = fileEncoding;
    }

    /**
     * Gets the encoding.
     * @return the encoding
     * @see #setEncoding(String) setEncoding()
     * @see #getEncoding getEncoding()
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Returns a <code>FileSelector</code> which can be used to receive
     * the matching files.
     * @return instance of <code>FileSelector</code> which provides all matching
     * 		files
     */
    public FileSelector getSelector() {
        return new FileSelector(directory, pattern);
    }

    /**
     * Sets the directory provider. The directory provider is used to browse
     * for matching files.
     * @param directory selected directory
     * @see #getDirectory() getProvider
     */
    public void setDirectory(final File directory) {
        this.directory = directory;
    }

    /**
     * Sets the file pattern. The file pattern must be a valid regular expression.
     * @param fileNamePattern the pattern to set
     * @see #getPattern() getPattern()
     */
    public void setPattern(final String fileNamePattern) {
        this.pattern = fileNamePattern;
    }

    /**
     * Gets the file pattern.
     * @return the selected pattern
     *
     * @see #setPattern(String) setPattern()
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Returns the selected directory.
     * @return the selected directory
     * @see #setDirectory(File) setProvider
     */
    public File getDirectory() {
        return directory;
    }
}
