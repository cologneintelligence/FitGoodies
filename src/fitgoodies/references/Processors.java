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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 * Class to manage registered processors.
 *
 * @author jwierum
 * @version $Id$
 */
public class Processors {
	private final List<AbstractCrossReferenceProcessor> processors =
		new ArrayList<AbstractCrossReferenceProcessor>();

	private String pattern;
	private Pattern searchPattern;
	private Pattern extractPattern;

	/**
	 * Default constructor. Does nothing.
	 */
	Processors() {
	}

	/**
	 * Adds a processor to the list of available processors.
	 * @param processor processor to add
	 */
	public final void add(final AbstractCrossReferenceProcessor processor) {
		processors.add(processor);
		rebuildPattern();
	}

	/**
	 * The number of elements.
	 * @return the length of the processors list
	 */
	public final int count() {
		return processors.size();
	}

	/**
	 * Remove the <code>i</code>-th processor from the processors list.
	 * @param i number of element to remove
	 */
	public final void remove(final int i) {
		processors.remove(i);
		rebuildPattern();

	}

	/**
	 * Remove the processor <code>processor</code> from the processor list.
	 * @param processor instance to remove
	 */
	public final void remove(final AbstractCrossReferenceProcessor processor) {
		processors.remove(processor);
		rebuildPattern();
	}

	/**
	 * Get the <code>i</code>-th processor from the processor list.
	 * @param i number of processor
	 * @return <code>i</code>-th processor
	 */
	public final AbstractCrossReferenceProcessor get(final int i) {
		return processors.get(i);
	}

	private void rebuildPattern() {
		StringBuilder patternString = new StringBuilder();

		for (AbstractCrossReferenceProcessor proc : processors) {
			if (patternString.length() != 0) {
				patternString.append("|");
			}
			patternString.append(proc.getPattern());
		}

		pattern = patternString.toString();

		searchPattern = null;
		extractPattern = null;
	}

	/**
	 * Generates a pattern which checks whether a string contains a cross reference.
	 * @return a pattern which matches cross references
	 */
	public final Pattern getSearchPattern() {
		if (searchPattern == null) {
			searchPattern = Pattern.compile("\\$\\{(" + pattern + ")\\}");
		}
		return searchPattern;
	}

	/**
	 * Generates a pattern which checks whether a string begins with a cross reference.
	 * @return a pattern which matches cross references at the beginning of a string
	 */
	public final Pattern getExtractPattern() {
		if (extractPattern == null) {
			extractPattern = Pattern.compile("^\\$\\{(" + pattern + ")\\}");
		}
		return extractPattern;
	}
}
