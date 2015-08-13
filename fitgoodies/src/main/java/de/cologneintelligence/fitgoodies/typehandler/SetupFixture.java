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

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Fixture that contains one single command: load.
 * <p/>
 * The load command loads an ActionFixture by its name.
 */
public class SetupFixture extends ActionFixture {
	/**
	 * Calls {@link #load} using the 2. column as argument.
	 *
	 * @throws Exception propagated to fit
	 */
	public void load() throws Exception {
		transformAndEnter();
	}

	/**
	 * Registers the {@link TypeHandler} which is provided by
	 * <code>className</code>. After processing this row, the TypeAdapter
	 * will be automatically used when the destination type matches
	 * {@link TypeHandler#getType()}.
	 *
	 * @param className fully qualified name of a class
	 * @throws Exception propagated to fit, in case of unknown classes,
	 *                   permission errors, etc.
	 */
	@SuppressWarnings("unchecked")
	public void load(final String className) throws Exception {
		Class<? extends TypeHandler<?>> clazz =
				(Class<? extends TypeHandler<?>>) Class.forName(className);

		TypeHandlerFactory helper = DependencyManager.getOrCreate(
				TypeHandlerFactory.class);
		helper.register(clazz);
	}
}
