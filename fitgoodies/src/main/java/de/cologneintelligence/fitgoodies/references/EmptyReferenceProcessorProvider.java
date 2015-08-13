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


package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.checker.Checker;
import de.cologneintelligence.fitgoodies.checker.EmptyChecker;
import de.cologneintelligence.fitgoodies.checker.NullChecker;
import de.cologneintelligence.fitgoodies.references.CellProcessor;
import de.cologneintelligence.fitgoodies.references.CellProcessorProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes ${empty()} and ${nonEmpty()}.
 * It just checks whether the object contains any value.
 */
public class EmptyReferenceProcessorProvider implements CellProcessorProvider {
	private static final Pattern PATTERN = Pattern.compile(
			"\\$\\{?(no[nt]Empty|empty|null|notNull)(?:(?:\\(\\))?\\})?", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean canProcess(String strippedText) {
		return strippedText != null && PATTERN.matcher(strippedText).find();
	}

	@Override
	public CellProcessor create(final String strippedText) {

		Matcher matcher = PATTERN.matcher(strippedText);
		matcher.find();

		final String method = matcher.group(1);

		return new CellProcessor() {
			@Override
			public String preprocess() {
				return null;
			}

			@Override
			public boolean replacesCheckRoutine() {
				return true;
			}

			@Override
			public Checker getChecker() {
				switch (method.toLowerCase()) {
					case "null":
						return new NullChecker(true);
					case "notnull":
						return new NullChecker(false);
					case "empty":
						return new EmptyChecker(true);
					case "nonempty":
					case "notempty":
						return new EmptyChecker(false);
					default:
						throw new IllegalArgumentException("Unknwown method: '" + method + "'");
				}
			}
		};
	}
}
