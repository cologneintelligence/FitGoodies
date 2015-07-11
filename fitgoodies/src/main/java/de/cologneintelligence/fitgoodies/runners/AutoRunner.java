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


package de.cologneintelligence.fitgoodies.runners;

import java.io.File;

/**
 * Simple main file which decides which available runner will be used.
 * Useful for command line invokes.<p>
 * Usage: {@code java -classpath /path/to/fit.jar:/path/to/fitgoodies.jar:...
 * fitgoodies.runners.AutoRunner input output [encoding]}
 */
@Deprecated
public final class AutoRunner {
    private AutoRunner() {
    }

    private static void error() {
        throw new IllegalArgumentException("Usage:\n" +
                "AutoRunner input output [encoding]\n" +
                "input    - input file or directory\n" +
                "output   - output file or directory\n" +
                "encoding - encoding to use\n");
    }

    /**
     * Entry point.
     * Calls either a {@link DirectoryRunner} or a {@link FileRunner}, depending
     * of the type of the first parameter (directory / file).
     *
     * @param args program parameters
     * @throws Throwable if an error occours
     */
    public static void main(final String[] args) throws Throwable {
        if (args.length < 2 || args.length > 3) {
            error();
        }

        File input = new File(args[0]);

        if (input.isDirectory()) {
            DirectoryRunner.main(args);
        } else if (input.isFile()) {
            FileRunner.main(args);
        } else {
            error();
        }
    }
}
