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

import java.util.ArrayList;
import java.util.List;

/**
 * $Id: DirectoryHelperMock.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public final class DirectoryHelperMock extends AbstractDirectoryHelper {
	private final List<String> mkPathes = new ArrayList<String>();

	public String[] getPathes() {
		return mkPathes.toArray(new String[]{});
	}

	@Override
	public boolean mkDir(final String path) {
		mkPathes.add(path);
		return true;
	}

	@Override
	public String separator() {
		return "/";
	}

	@Override
	public int dirDepth(final String relPath) {
		return relPath.length() - relPath.replaceAll("\\/", "").length();
	}

	@Override
	public String getFilename(final String file) {
		if (file.indexOf('/') == -1) {
			return file;
		} else {
			return file.substring(file.lastIndexOf('/') + 1);
		}
	}

	@Override
	public String getDir(final String file) {
		if (file.indexOf('/') == -1) {
			return "";
		} else {
			return file.substring(0, file.lastIndexOf('/') + 1);
		}
	}

	@Override
	public boolean isAbsolutePath(final String path) {
		return path.startsWith("/");
	}
};