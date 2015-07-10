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

/**
 * This runner traverses a directory tree. All files that end with
 * .htm or .html are processed. Files which are named setup.html are processed
 * as the first file in the directory, files which are named teardown.html are
 * processed as last. These files are <em>not</em> processed before each html
 * file.<br /><br />
 *
 * All processed files are copied into an output folder. Additionally, a report
 * file is generated.
 *
 * @deprecated This method is deprecated! Use a {@link de.cologneintelligence.fitgoodies.runners.FitRunner} instead.
 *
 */
@Deprecated
public class DirectoryRunner {
	public static void main(final String[] args) throws Throwable {
		if (args.length < 2) {
			final String error = "Usage:\n"
					+ "fitgoodies.runners.DirectoryRunner inputdir outputdir [encoding]";
			System.err.println(error);
			throw new RuntimeException(error);
		}

		String encoding = "utf-8";
		boolean includeEncoding = false;

		if (args.length > 2) {
			includeEncoding = true;
			encoding = args[2];
		}

		System.err.println();
		System.err.println("WARNING: This main method is deprecated! Please run this test with:");
		System.err.println(String.format("java %s --source \"%s\" --destination \"%s\" %s",
				FitRunner.class.getName(), args[0], args[1], includeEncoding ? "--encoding " + encoding : ""));
		System.err.println();

		FitRunner.main(new String[]{
				"--source", args[0],
				"--destination", args[1],
				"--encoding", encoding
		});
	}
}
