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

package de.cologneintelligence.fitgoodies.testsupport;

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

class CellAtMatcher extends BaseMatcher<FitCell> {
	private final int x;
	private final int y;
    private final FitCell cell;

    public CellAtMatcher(FitTable parse, int row, int col) {
        this.cell = parse.rows().get(row).cells().get(col);
		this.x = row;
		this.y = col;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("the cell at position %d/%d (%s)", x, y, cell));
	}

	@Override
	public boolean matches(Object item) {
		return item == cell;
	}

	public static Matcher<FitCell> cellAt(FitTable table, int row, int col) {
		return new CellAtMatcher(table, row, col);
	}
}
