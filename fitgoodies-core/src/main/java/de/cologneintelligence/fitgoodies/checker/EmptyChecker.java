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

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;

public class EmptyChecker implements Checker {
	private final boolean expectEmpty;

	public EmptyChecker(boolean expectEmpty) {
		this.expectEmpty = expectEmpty;
	}

	@Override
	public Object check(FitCell cell, String input, ValueReceiver valueReceiver, TypeHandler typeHandler) {
		Object result = null;

		try {
			result = valueReceiver.get();
			boolean isEmpty = isEmpty(result);

			if (expectEmpty) {
				if (isEmpty) {
                    cell.right();
				} else {
                    cell.wrong(typeHandler.toString(result));
				}
			} else {
				if (!isEmpty) {
                    cell.right(typeHandler.toString(result));
				} else {
                    cell.wrong("(empty)");
				}
			}
		} catch (Exception e) {
            cell.exception(e);
		}

		return result;
	}

	protected boolean isEmpty(Object result) {
		return result == null || (result instanceof String && result.equals(""));
	}

	public boolean isExpectEmpty() {
		return expectEmpty;
	}
}
