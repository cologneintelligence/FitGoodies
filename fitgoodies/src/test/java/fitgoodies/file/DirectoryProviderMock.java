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


package fitgoodies.file;

import java.util.Iterator;

/**
 * $Id$
 * @author jwierum
 */
public class DirectoryProviderMock implements DirectoryProvider {
	@Override
	public final Iterator<FileInformation> getFiles() {
		return new FileIterator(new FileInformation[] {
				new FileInformationMock("/test", "file1.txt", "this is file 1".getBytes()),
				new FileInformationMock("/test", "file2.txt", "this is file 2".getBytes()),
				new FileInformationMock("/test", "file3.txt", "this is file 3".getBytes()),
				new FileInformationMock("/", "f.txt.bat", "this is the batch file".getBytes()),
				new FileInformationMock("/dir/dir2", "noext", "this is another file".getBytes())
			});
	}

	@Override
	public final Iterator<DirectoryProvider> getDirectories() {
		return null;
	}

	@Override
	public final String getPath() {
		return "/test";
	}
}
