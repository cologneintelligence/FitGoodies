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

import fit.Counts;

/**
 * Interface which is used to check a single test file.
 *
 * @author jwierum
 * @version $Id$
 */
public interface Runner {
	/**
	 * Processes <code>inputFile</code>, write output to <code>outputFile</code>
	 * and return the resulting counts.
	 * @param inputFile file to process
	 * @param outputFile file to write output to
	 * @return resulting counts
	 */
	Counts run(String inputFile, String outputFile);

	/**
	 * Sets the encoding of all input/output files to <code>encoding</code>.
	 * @param encoding encoding to be used
	 * @see #getEncoding()
	 */
	void setEncoding(String encoding);

	/**
	 * Gets the encoding of all input/output files.
	 * @return encoding to be used
	 * @see #setEncoding(String) setEncoding(String)
	 */
	String getEncoding();
}
