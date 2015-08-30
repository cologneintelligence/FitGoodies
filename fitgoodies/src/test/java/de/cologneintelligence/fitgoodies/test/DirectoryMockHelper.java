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

package de.cologneintelligence.fitgoodies.test;


import de.cologneintelligence.fitgoodies.file.SimpleRegexFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DirectoryMockHelper {
	private Map<String, Object> dirs = new LinkedHashMap<>();

	public void addFile(final String file) {
		String[] parts = file.split("[/\\\\]");
		Map<String, Object> currentDir = dirs;

		for (int i = 0; i < parts.length - 1; i++) {
			final String currentPart = parts[i];
			if (!currentDir.containsKey(currentPart)) {
				currentDir.put(currentPart, new LinkedHashMap<String, Object>());
			}

			@SuppressWarnings("unchecked")
			final Map<String, Object> tmp = (Map<String, Object>) currentDir.get(currentPart);
			currentDir = tmp;
		}

		final String fileName = parts[parts.length - 1];

		File fileMock = mock(File.class, file);
		when(fileMock.isDirectory()).thenReturn(false);
		when(fileMock.isFile()).thenReturn(true);
		when(fileMock.getName()).thenReturn(fileName);
		when(fileMock.getPath()).thenReturn(file);
		when(fileMock.toString()).thenReturn(file);
		when(fileMock.getAbsoluteFile()).thenReturn(fileMock);

		currentDir.put(fileName, fileMock);
	}

	public File finishMock(String selector) {
		return createTree(selector, "", "", dirs);
	}

	private File createTree(String selector, String parent, String name, Map<String, Object> dirs) {
		List<File> children = new LinkedList<>();
		List<File> files = new LinkedList<>();

		for (Map.Entry<String, Object> child : dirs.entrySet()) {
			if (child.getValue() instanceof File) {
				children.add((File) child.getValue());
				files.add((File) child.getValue());
			} else {
				@SuppressWarnings("unchecked")
				final Map<String, Object> childDirs = (Map<String, Object>) child.getValue();
				children.add(createTree(selector, parent + "/" + name, child.getKey(), childDirs));
			}
		}

		File dir = mock(File.class, name);

		for (File child : children) {
			when(child.getParent()).thenReturn(parent + "/" + name);
			when(child.getParentFile()).thenReturn(dir);
		}

		final File[] allChildren = children.toArray(new File[children.size()]);
		final File[] fileChildren = files.toArray(new File[files.size()]);

		if (selector == null) {
			when(dir.listFiles(argThat(any(SimpleRegexFilter.class)))).thenReturn(fileChildren);
		} else {
			when(dir.listFiles((FileFilter) argThat(allOf(
					instanceOf(SimpleRegexFilter.class), hasProperty("pattern", equalTo(selector))))))
					.thenReturn(fileChildren);
		}

		when(dir.listFiles()).thenReturn(allChildren);
		when(dir.getName()).thenReturn(name);
		when(dir.toString()).thenReturn(name);
		when(dir.isDirectory()).thenReturn(true);
		when(dir.isFile()).thenReturn(false);

		return dir;
	}
}
