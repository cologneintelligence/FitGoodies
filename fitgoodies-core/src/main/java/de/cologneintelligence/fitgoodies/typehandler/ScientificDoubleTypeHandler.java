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

package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.types.ScientificDouble;

public class ScientificDoubleTypeHandler extends TypeHandler<ScientificDouble> {

	public ScientificDoubleTypeHandler(String convertParameter) {
		super(convertParameter);
	}

	@Override
	public Class<ScientificDouble> getType() {
		return ScientificDouble.class;
	}

	@Override
	public ScientificDouble unsafeParse(String input) {
		return ScientificDouble.valueOf(input);
	}
}
