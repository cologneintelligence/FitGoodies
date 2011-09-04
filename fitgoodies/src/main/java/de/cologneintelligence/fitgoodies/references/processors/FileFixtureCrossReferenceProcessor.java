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


package de.cologneintelligence.fitgoodies.references.processors;

import java.io.FileNotFoundException;

import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.references.CrossReference;


/**
 * This fixture can be used to retrieve a filename, which was selected earlier
 * using either the {@link de.cologneintelligence.fitgoodies.file.FileFixtureHelper} or a
 * {@link de.cologneintelligence.fitgoodies.file.FileFixture}.<br /><br />
 * This class provide ${selectedFile()} and ${selectedEncoding()}.
 *
 * @author jwierum
 * @version $Id$
 */
public class FileFixtureCrossReferenceProcessor extends
		AbstractCrossReferenceProcessor {
	private static final String PATTERN = "(selectedFile|selectedEncoding)\\(\\)";

	/**
	 * Default constructor.
	 */
	public FileFixtureCrossReferenceProcessor() {
		super(PATTERN);
	}

	/**
	 * A user friendly description.
	 * @return a description.
	 */
	@Override
	public final String info() {
		return "provides selectedFile() and selectedEncoding()";
	}

	/**
	 * Replaces the match with the selected file's name or encoding.
	 * @param cr the extracted match
	 * @param object ignored
	 * @return if the command was selectedFile, the first matching file is returned,
	 * 		if it was selectedEncoding, the selected encoding is returned
	 */
	@Override
	public final String processMatch(final CrossReference cr, final Object object) {
		if (cr.getCommand().equals("selectedEncoding")) {
			return FileFixtureHelper.instance().getEncoding();
		} else {
			try {
				return FileFixtureHelper.instance().getSelector().getFirstFile().filename();
			} catch (FileNotFoundException e) {
				throw new RuntimeException("no file found");
			}
		}
	}
}
