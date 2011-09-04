/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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
 * Useful for command line invokes.<br /><br />
 * Usage: <code>java -classpath /path/to/fit.jar:/path/to/fitgoodies.jar:...
 * fitgoodies.runners.AutoRunner input output [encoding]</code>
 *
 * @author jwierum
 * @version $Id$
 */
public final class AutoRunner {
	private AutoRunner() { }

	private static void error() {
		StringBuilder msg = new StringBuilder();
		msg.append("Usage:\n");
		msg.append("AutoRunner input output [encoding]\n");
		msg.append("input    - input file or directory\n");
		msg.append("output   - output file or directory\n");
		msg.append("encoding - encoding to use\n");
		throw new IllegalArgumentException(msg.toString());
	}

	/**
	 * Entry point.
	 * Calls either a {@link DirectoryRunner} or a {@link FileRunner}, depending
	 * of the type of the first parameter (directory / file).
	 *
	 * @param args program parameters
	 */
	public static void main(final String[] args) {
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
