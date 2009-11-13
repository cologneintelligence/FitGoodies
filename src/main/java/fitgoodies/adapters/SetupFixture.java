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


package fitgoodies.adapters;

import fitgoodies.ActionFixture;

/**
 * Fixture that contains one single command: load.
 *
 * The load command loads an ActionFixture by its name.
 *
 * @author jwierum
 * @version $Id$
 */
public class SetupFixture extends ActionFixture {
	/**
	 * Calls {@link #load} using the 2. column as argument.
	 * @throws Exception propagated to fit
	 */
	public void load() throws Exception {
		transformAndEnter();
	}

	/**
	 * Registers the {@link AbstractTypeAdapter} which is provided by
	 * <code>className</code>. After processing this row, the TypeAdapter
	 * will be automatically used when the destination type matches
	 * {@link AbstractTypeAdapter#getType()}.
	 *
	 * @param className fully qualified name of a class
	 * @throws Exception propagated to fit, in case of unknown classes,
	 * 			permission errors, etc.
	 */
	@SuppressWarnings("unchecked")
	public final void load(final String className) throws Exception {
		Class<? extends AbstractTypeAdapter<?>> clazz =
			(Class<? extends AbstractTypeAdapter<?>>) Class.forName(className);

		TypeAdapterHelper.instance().register(clazz);
	}
}
