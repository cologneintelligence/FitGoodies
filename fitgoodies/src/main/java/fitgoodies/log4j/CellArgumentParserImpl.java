/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package fitgoodies.log4j;

import java.util.HashMap;
import java.util.Map;

import fit.Parse;
import fitgoodies.util.FixtureTools;

/**
 * Removes the parameters from a cell value and stores it in a <code>Map</code>.
 * @author jwierum
 * @version $Id$
 *
 */
public final class CellArgumentParserImpl implements CellArgumentParser {
	private final String cellParameters;

	/**
	 * Creates a new <code>CellArgumentParserImpl</code> which processes <code>
	 * cell</code>.
	 * @param cell the cell to process
	 */
	public CellArgumentParserImpl(final Parse cell) {
		cellParameters = FixtureTools.extractCellParameter(cell);
	}

	/**
	 * Gets the extracted parameters.
	 * @return key/value pair of parameters
	 */
	@Override
	public Map<String, String> getExtractedCommandParameters() {
		Map<String, String> result = new HashMap<String, String>();

		if (cellParameters != null) {
			arrayToMap(cellParameters.split("\\s*,\\s*"), result);
		}

		return result;
	}

	private void arrayToMap(final String[] parts, final Map<String, String> result) {
		for (String part : parts) {
			String[] keyAndValue = part.split("\\s*=\\s*", 2);
			storeInMap(result, keyAndValue);
		}
	}

	private void storeInMap(final Map<String, String> result, final String[] keyAndValue) {
		final int KEY = 0;
		final int VALUE = 1;

		if (keyAndValue.length != 2) {
			throw new IllegalArgumentException("Invalid parameter format: "
					+ keyAndValue[KEY]);
		} else {
			result.put(keyAndValue[KEY].toLowerCase(), keyAndValue[VALUE]);
		}
	}
}
