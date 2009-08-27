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


package fitgoodies.references;

import java.util.regex.Matcher;

import fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import fitgoodies.references.processors.EmptyCrossReferenceProcessor;
import fitgoodies.references.processors.FileFixtureCrossReferenceProcessor;
import fitgoodies.references.processors.PropertyCrossReferenceProcessor;
import fitgoodies.references.processors.StorageCrossReferenceProcessor;

/**
 * Singleton class to manage registered {@link AbstractCrossReferenceProcessor}s.
 *
 * @see SetupFixture SetupFixture
 * @author jwierum
 * @version $Id$
 */

public final class CrossReferenceHelper {
	private static CrossReferenceHelper instance;
	private final Processors processors = new Processors();

	private CrossReferenceHelper() {
		processors.add(new EmptyCrossReferenceProcessor());
		processors.add(new StorageCrossReferenceProcessor());
		processors.add(new FileFixtureCrossReferenceProcessor());
		processors.add(new PropertyCrossReferenceProcessor());
	};

	/**
	 * Returns the instance of <code>CrossReferenceHelper</code>.
	 * @return an instance of <code>CrossReferenceHelper</code>
	 */
	public static CrossReferenceHelper instance() {
		if (instance == null) {
			instance = new CrossReferenceHelper();
		}
		return instance;
	}

	/**
	 * Resets the processors to the default ones.
	 */
	public static void reset() {
		instance = null;
	}

	/**
	 * Gets the processor list.
	 * @return a list of registered processors.
	 */
	public Processors getProcessors() {
		return processors;
	}

	/**
	 * Checks whether <code>string</code> contains one or more cross references.
	 * @param string string to check
	 * @return true whether at least one cross reference is present, false otherwise
	 */
	public boolean containsCrossReference(final String string) {
		Matcher m = processors.getSearchPattern().matcher(string);
		return m.find();
	}

	/**
	 * Extracts a cross reference from the beginning of <code>string</code>.
	 * @param string string to process
	 * @return <code>CrossReference</code> object which holds the extracted
	 * 		reference
	 */
	public CrossReference getCrossReference(final StringBuilder string) {
		CrossReference result = null;

		Matcher m = processors.getExtractPattern().matcher(string);
		if (m.find()) {
			String match = m.group(1);
			for (int i = 0; i < processors.count(); ++i) {
				result = processors.get(i).extractCrossReference(match);

				if (result != null) {
					break;
				}
			}

			string.delete(0, m.group(0).length());
		}

		return result;
	}

	/**
	 * Parses a string and resolves all occurring cross references.
	 * @param text cell text to process
	 * @param object actual object which is compared with the cell afterwards
	 * @return the new text to compare
	 * @throws CrossReferenceProcessorShortcutException thrown if no more comparison is needed
	 */
	public String parseBody(final String text, final Object object)
			throws CrossReferenceProcessorShortcutException {
		String returnValue = text;

		if (containsCrossReference(text)) {
			StringBuilder todo = new StringBuilder(text);
			StringBuilder result = new StringBuilder();

			while (todo.length() > 0) {
				if (todo.charAt(0) == '$') {
					CrossReference cr = getCrossReference(todo);

					if (cr != null) {
						result.append(processCrossReference(cr, object));
					} else {
						result.append('$');
						todo.deleteCharAt(0);
					}
				} else {
					result.append(todo.charAt(0));
					todo.deleteCharAt(0);
				}
			}

			returnValue = result.toString();
		}
		return returnValue;
	}

	private String processCrossReference(final CrossReference cr, final Object object)
			throws CrossReferenceProcessorShortcutException {
		return cr.getProcessor().processMatch(cr, object);
	}
}
