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
 * This runner processes a single file. No additional report is generated.
 *
 * @deprecated This method is deprecated! Use a {@link de.cologneintelligence.fitgoodies.runners.FitRunner} instead.
 *
 */

@Deprecated
public final class FileRunner {
	private FileRunner() { }

	/**
	 * Entry point.
	 * Takes 2 or 3 arguments, either the input file and the output file,
	 * or the input file, the output file and the encoding. If the encoding
	 * is omitted, utf-8 is used.
	 *
	 * @param args program parameters
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length < 2) {
			final String error = "Usage:\n"
				+ "fitgoodies.runners.FileRunner inputfile outputfile [encoding]";
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
		System.err.println(String.format("java %s --file \"%s\" --destination \"%s\" %s",
				FitRunner.class.getName(), args[0], args[1], includeEncoding ? "--encoding " + encoding : ""));
		System.err.println();

		FitRunner.main(new String[]{
				"--file", args[0],
				"--destination", args[1],
				"--encoding", encoding
		});
	}
}
