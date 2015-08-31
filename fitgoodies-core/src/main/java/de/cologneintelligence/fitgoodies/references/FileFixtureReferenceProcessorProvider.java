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


package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This fixture can be used to retrieve a filename, which was selected earlier
 * using either the {@link de.cologneintelligence.fitgoodies.file.FileFixtureHelper} or a
 * {@link de.cologneintelligence.fitgoodies.file.FileFixture}.
 * <p>
 * This class provide ${selectedFile()} and ${selectedEncoding()}.
 */
public class FileFixtureReferenceProcessorProvider implements CellProcessorProvider {
	private static final Pattern PATTERN = Pattern.compile(
			"\\$\\{(selectedFile|selectedEncoding)(?:\\(\\))?\\}",
			Pattern.CASE_INSENSITIVE);
	private final FileFixtureHelper fileFixtureHelper;

	public FileFixtureReferenceProcessorProvider() {
		fileFixtureHelper = DependencyManager.getOrCreate(FileFixtureHelper.class);
	}

	@Override
	public boolean canProcess(String strippedText) {
		return strippedText != null && PATTERN.matcher(strippedText).find();
	}

	@Override
	public CellProcessor create(final String strippedText) {
		return new CellProcessor() {
			@Override
			public String preprocess() {
				final Matcher matcher = PATTERN.matcher(strippedText);
				matcher.find();

				final String select = matcher.group(1);

				String result;
				if (select.equalsIgnoreCase("selectedEncoding")) {
					result = fileFixtureHelper.getEncoding();
				} else {
					try {
						result = fileFixtureHelper.getSelector().getFirstFile().getName();
					} catch (FileNotFoundException e) {
						result = "error: no file selected";
					}
				}

				return strippedText.replaceAll(Pattern.quote(matcher.group(0)), result);
			}
		};
	}
}
