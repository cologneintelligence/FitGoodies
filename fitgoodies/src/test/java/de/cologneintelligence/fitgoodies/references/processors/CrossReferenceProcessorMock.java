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


package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 * $Id$
 * @author jwierum
 */
public class CrossReferenceProcessorMock extends AbstractCrossReferenceProcessor {
	private boolean calledExtract;
	private boolean calledPattern;
	private boolean calledProcess;
	private boolean calledInfo;
	private CrossReference cachedResult;
	private final String match;

	public CrossReferenceProcessorMock(final String matchString) {
		super(matchString);
		this.match = matchString;
	}

	public final boolean isCalledExtract() {
		return calledExtract;
	}

	public final boolean isCalledPattern() {
		return calledPattern;
	}

	public final boolean isCalledProcess() {
		return calledProcess;
	}

	public final boolean isCalledInfo() {
		return calledInfo;
	}

	public final CrossReference getCachedResult() {
		return cachedResult;
	}

	public final void reset() {
		calledExtract = false;
		calledPattern = false;
		calledProcess = false;
	}

	@Override
	public final CrossReference extractCrossReference(final String ignored) {
		calledExtract = true;
		cachedResult = new CrossReference(match, null, null, this);
		return cachedResult;
	}

	@Override
	public final String getPattern() {
		calledPattern = true;
		return super.getPattern();
	}

	@Override
	public final String processMatch(final CrossReference cr, final Object object) {
		calledProcess = true;

		if (cr == cachedResult) {
			return "matched";
		} else {
			return "error";
		}
	}

	@Override
	public final String info() {
		return match;
	}
}
