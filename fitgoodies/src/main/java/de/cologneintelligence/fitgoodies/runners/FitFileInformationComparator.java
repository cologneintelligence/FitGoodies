/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.file.FileInformation;

import java.io.File;


/**
 * Compares two FileInformation objects. They are sorted in alphabetical order,
 * with two exceptions: files with the name &quot;setup.html&quot; are the first
 * file in the directory, files named &quot;teardown.html&quot; are processed as
 * the last item in a directory
 */
public class FitFileInformationComparator extends FitFilenameComparator<FileInformation> {
	@Override
	protected File getFile(FileInformation lhs) {
		return lhs.getFile();
	}

	@Override
	protected String getFileName(FileInformation fo) {
		return fo.getFile().getName();
	}
}
