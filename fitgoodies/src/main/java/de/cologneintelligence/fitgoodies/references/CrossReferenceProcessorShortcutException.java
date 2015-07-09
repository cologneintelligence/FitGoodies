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

/**
 * This exception is thrown if a cross reference processor wants to stop
 * checking a cell's content. Most of the time, a cross reference processor
 * changes the cell's content and compares the new content to the object's value.
 *
 * However, there are cases where the processor can decide whether the value
 * is correct or not. In that case, there's no need to call <code>check</code>.
 * In these cases, this exception can be thrown.
 * See {@link de.cologneintelligence.fitgoodies.references.processors.EmptyCrossReferenceProcessor} for
 * an example.
 *
 */
public class CrossReferenceProcessorShortcutException extends Exception {
	private static final long serialVersionUID = -8237888733374923655L;
	private final boolean testOk;

	/**
	 * Initializes a new exception.
	 * @param wasOk indicates whether fit should mark the cell as right/green
	 * 		(<code>true</code>) or wrong/red (<code>false</code>)
	 * @param message the message which will be added to the cell
	 */
	public CrossReferenceProcessorShortcutException(final boolean wasOk,
			final String message) {
		super(message);
		this.testOk = wasOk;
	}

	/**
	 * Determines whether the cell is right or wrong.
	 * @return true if the cell was right, false otherwise
	 */
	public final boolean isOk() {
		return testOk;
	}
}
