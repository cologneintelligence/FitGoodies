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


package fitgoodies.references.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fitgoodies.references.CrossReference;
import fitgoodies.references.CrossReferenceProcessorShortcutException;

/**
 * The AbstractCrossReferenceProcessor is the base class for all reference
 * processors. Each cross reference processor must be capable to return a
 * pattern that matches the reference, extract a reference, resolve a reference
 * and give a small user output.
 *
 * @author jwierum
 * @version $Id: AbstractCrossReferenceProcessor.java 185 2009-08-17 13:47:24Z jwierum $
 */
public abstract class AbstractCrossReferenceProcessor {
	private final String plainPattern;
	private final Pattern compiledPattern;

	/**
	 * Generates a new object. Stores the pattern for
	 * {@link #extractCrossReference(String)}.
	 * @param pattern the pattern the processor is responsible for
	 */
	public AbstractCrossReferenceProcessor(final String pattern) {
		plainPattern = pattern;
		compiledPattern = Pattern.compile("^" + getPattern() + "$");
	}

	/**
	 * Gets the saved pattern.
	 * @return the pattern which was provided when
	 * 		{@link #AbstractCrossReferenceProcessor(String)} was called
	 */
	public String getPattern() {
		return plainPattern;
	}

	/**
	 * Converts a match into a CrossReference.
	 * This method implies that the pattern is build in one of three ways:
	 *
	 * <ul>
	 * <li>A match has one group, which matches the command. Namespace and
	 * 		parameter are empty (<code>null</code>)</li>
	 * <li>A match has two groups: the first matches the command, the second
	 * 		matches the parameter. The namespace is empty (<code>null</code>)</li>
	 * <li>A match has three groups: the first matches the namespace, the
	 * 		second matches the command and the third matches the parameter(s)</li>
	 * </ul>
	 *
	 * In every other case, the method must be overridden with custom code.
	 *
	 * @param match the <code>String</code> that matched the pattern
	 * @return an instance of <code>CrossReference</code> or null on errors
	 */
	public CrossReference extractCrossReference(final String match) {
		Matcher m = compiledPattern.matcher(match);
		if (m.find()) {
			if (m.groupCount() == 1) {
				return new CrossReference(m.group(1), null, null, this);
			} else if (m.groupCount() == 2) {
				return new CrossReference(m.group(1), null, m.group(2), this);
			} else if (m.groupCount() == 3) {
				return new CrossReference(m.group(2), m.group(1), m.group(3), this);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * This method replaces the match with a string.
	 * @param cr the extracted cross reference
	 * @param object the object which will be compared with the new cell content
	 * @return the replacement of the cross reference.
	 * @throws CrossReferenceProcessorShortcutException can be thrown to skip
	 * 		the comparison. See
	 * 		{@link fitgoodies.references.CrossReferenceProcessorShortcutException}
	 * 		for more information.
	 * @see #extractCrossReference(String) extractCrossReference(String)
	 */
	public abstract String processMatch(CrossReference cr, Object object)
		throws CrossReferenceProcessorShortcutException;

	/**
	 * Returns a short, user-friendly description.
	 * @return a description of the processor.
	 */
	public abstract String info();
}
