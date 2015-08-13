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

package de.cologneintelligence.fitgoodies.external;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Fixture that contains one single command: addProperty.
 * <p/>
 * This adds Properties as Strings to a list which used by StartFixture
 * to pass it to the external command. Example: ant target -Dtestkey=testvalue.
 *
 * @author kia
 */
public class SetupFixture extends ActionFixture {

	/**
	 * Calls {@link #addProperty} using the 2. column as argument.
	 *
	 * @throws Exception propagated to fit
	 */
	public void addProperty() throws Exception {
		transformAndEnter();
	}

	/**
	 * @param property
	 * @throws Exception propagated to fit
	 */
	public void addProperty(String property) throws Exception {
		SetupHelper setupHelper = DependencyManager.getOrCreate(SetupHelper.class);
		setupHelper.addProperty(property);
	}
}
