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

package de.cologneintelligence.fitgoodies.checker;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;

public class EqualityChecker implements Checker {
	@SuppressWarnings("unchecked")
    @Override
	public Object check(Parse cell, Counts counts, String input, ValueReceiver valueReceiver, TypeHandler typeHandler) {
		Object result = null;

		try {
			if (valueReceiver == null) {
				FitUtils.ignore(cell);
				counts.ignores++;
			} else {
				result = valueReceiver.get();
				if (typeHandler.equals(typeHandler.parse(input), result)) {
					FitUtils.right(cell);
					counts.right++;
				} else {
					FitUtils.wrong(cell, typeHandler.toString(result));
					counts.wrong++;
				}
			}
		} catch (Exception e) {
			FitUtils.exception(cell, e);
			counts.exceptions++;
		}

		return result;
	}
}
