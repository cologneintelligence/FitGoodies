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
import java.io.File;
import java.util.regex.Pattern;

/**
 * Implementation of {@link AbstractDirectoryHelper} which uses the file system.
 *
 * @author jwierum
 * @version $Id: FileSystemDirectoryHelper.java 185 2009-08-17 13:47:24Z jwierum $
 */
public final class FileSystemDirectoryHelper extends AbstractDirectoryHelper {
	@Override
	public boolean mkDir(final String path) {
		return new File(path).mkdirs();
	}

	@Override
	public String separator() {
		return File.separator;
	}

	@Override
	public int dirDepth(final String path) {
		return path.length()
			- path.replaceAll(Pattern.quote(File.separator), "").length();
	}

	@Override
	public String getFilename(final String file) {
		return new File(file).getName();
	}

	@Override
	public String getDir(final String file) {
		String parent = new File(file).getParent();
		if (parent != null && parent.endsWith(File.separator)) {
			parent += File.separator;
		} else if (parent == null) {
			parent = "";
		}
		return parent;
	}

	@Override
	public boolean isAbsolutePath(final String path) {
		return new File(path).isAbsolute();
	}
}
