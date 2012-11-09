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


package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 * Represents a cross reference match.
 *
 * @author jwierum
 * @version $Id$
 */

public class CrossReference {
	private final String command;
	private final String namespace;
	private final String parameter;
	private final AbstractCrossReferenceProcessor processor;

	/**
	 * Initializes a new <code>CrossReference</code> object.
	 * @param extractedCommand the represented command
	 * @param extractedNamespace the represented namespace (or null)
	 * @param extractedParameter the represented parameter(s) (or null)
	 * @param responsibleProcessor the processor which extracted the match
	 */
	public CrossReference(final String extractedCommand,
			final String extractedNamespace,
			final String extractedParameter,
			final AbstractCrossReferenceProcessor responsibleProcessor) {
		this.command = extractedCommand;
		this.namespace = extractedNamespace;
		this.parameter = extractedParameter;
		this.processor = responsibleProcessor;
	}

	/**
	 * Returns the saved command.
	 * @return the extracted command
	 */
	public final String getCommand() {
		return command;
	}

	/**
	 * Returns the saved namespace.
	 * @return the extracted namespache or null
	 */
	public final String getNamespace() {
		return namespace;
	}

	/**
	 * Returns the save parameter.
	 * @return the extracted parameters or null
	 */
	public final String getParameter() {
		return parameter;
	}

	/**
	 * Returns the responsible processor.
	 * @return the responsible processor
	 */
	public final AbstractCrossReferenceProcessor getProcessor() {
		return processor;
	}
}
