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

import fitgoodies.references.CrossReference;
import fitgoodies.references.CrossReferenceProcessorShortcutException;

/**
 * Processes ${empty()} and ${nonEmpty()}.
 * It just checks whether the object contains any value.
 *
 * @author jwierum
 * @version $Id$
 */
public class EmptyCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
	private static final String PATTERN = "(nonEmpty|empty)\\(\\)";

	/**
	 * Default constructor.
	 */
	public EmptyCrossReferenceProcessor() {
		super(PATTERN);
	}

	private static boolean isEmptyObject(final Object object) {
		return object == null || (object instanceof String && ((String) object).equals(""));
	}

	private void processNonEmpty(final Object object)
			throws CrossReferenceProcessorShortcutException {
		if (!isEmptyObject(object)) {
			throw new CrossReferenceProcessorShortcutException(true, "value is non-empty");
		} else {
			throw new CrossReferenceProcessorShortcutException(false, "value must not be empty!");
		}
	}

	private void processEmpty(final Object object)
			throws CrossReferenceProcessorShortcutException {
		if (isEmptyObject(object)) {
			throw new CrossReferenceProcessorShortcutException(true, "value is empty");
		} else {
			throw new CrossReferenceProcessorShortcutException(false,
					"value must be empty or null!");
		}
	}

	/**
	 * Processes empty and nonEmpty matches. In both cases, a
	 * {@link CrossReferenceProcessorShortcutException} is thrown.
	 *
	 * @param cr extracted cross reference
	 * @param object object to analyze
	 * @return null on errors (otherwise a <code>CrossReferenceProcessorShortcutException</code>)
	 * 		is thrown.
	 * @throws CrossReferenceProcessorShortcutException is thrown on every
	 * 		successful match. Provides information whether the object was
	 * 		not <code>null</code> or <code>null</code>.
	 */
	@Override
	public final String processMatch(final CrossReference cr, final Object object)
			throws CrossReferenceProcessorShortcutException {
		if (cr.getCommand().equals("empty")) {
			processEmpty(object);
		} else if (cr.getCommand().equals("nonEmpty")) {
			processNonEmpty(object);
		}
		return null;
	}

	/**
	 * A user friendly description.
	 * @return a description.
	 */
	@Override
	public final String info() {
		return "provides empty() and nonEmpty()";
	}
}
