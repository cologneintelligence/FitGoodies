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

import fit.Counts;

/**
 * This runner processes a single file. No additional report is generated.
 *
 * @author jwierum
 * @version $Id$
 */
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
	public static void main(final String[] args) {
		if (args.length < 2) {
			final String error = "Usage:\n"
				+ "fitgoodies.runners.FileRunner inputfile outputfile [encoding]";
			System.err.println(error);
			throw new RuntimeException(error);
		}

		try {
			String encoding = "utf-8";

			if (args.length > 2) {
				encoding = args[2];
			}

			Runner runner = new FitFileRunner();
			args[0] = args[0].replace('/', File.separatorChar).replace('\\', File.separatorChar);
			args[1] = args[1].replace('/', File.separatorChar).replace('\\', File.separatorChar);
			runner.setEncoding(encoding);
			Counts result = runner.run(args[0], args[1]);
			System.out.println(result);
			
			if (result != null && (result.exceptions > 0 || result.wrong > 0)) {
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
