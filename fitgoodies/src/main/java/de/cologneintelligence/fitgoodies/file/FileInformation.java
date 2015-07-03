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

import java.io.*;
import java.util.regex.Pattern;

/**
 * Represents a file and provides information about it.
 *
 */
public class FileInformation {

    private final File file;

    /**
     * Generates a new information object.
     */
    public FileInformation(final File file) {
        this.file = file;
    }

    /**
     * Opens the file and returns a <code>BufferedReader</code> object using
     * the given encoding.
     * @param encoding the encoding to use
     * @return the open file as <code>BufferdReader</code>
     * @throws IOException thrown if the file could not be read
     */
    public BufferedReader openBufferedReader(final String encoding) throws IOException {
        final InputStream fis = openInputStream();
        final InputStreamReader isr = new InputStreamReader(fis, encoding);
        return new BufferedReader(isr);
    }

    /**
     * Alias to {@link #getFile()}.toString().
     * @return the file's path and name
     */
    @Override
    public String toString() {
        return file.toString();
    }

    /**
     * The file's name.
     * @return the filename
     */
    public String filename() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }



    /**
     * Opens the file and returns a <code>InputStream</code> object.
     * @return the open file as <code>InputStream</code>
     * @throws IOException thrown if the file could not be read
     */
    public InputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public String[] getParts() {
        return file.getAbsolutePath().split(Pattern.quote(File.separator), -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInformation that = (FileInformation) o;

        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

}
