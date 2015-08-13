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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimedActionFixture extends ActionFixture {

	public DateFormat format = new SimpleDateFormat("hh:mm:ss");

	// Traversal ////////////////////////////////

	public void doTable(Parse table) {
		super.doTable(table);
		table.parts.parts.last().more = td("time");
		table.parts.parts.last().more = td("split");
	}

	protected void doCells(Parse cells) {
		Date start = time();
		super.doCells(cells);
		long split = time().getTime() - start.getTime();
		cells.last().more = td(format.format(start));
		cells.last().more = td(split < 1000 ? "&nbsp;" : Double.toString((split) / 1000.0));
	}

	// Utility //////////////////////////////////

	public Date time() {
		return new Date();
	}

	public Parse td(String body) {
		return new Parse("td", FitUtils.info(body), null, null);
	}

}
