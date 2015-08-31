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

package de.cologneintelligence.fitgoodies.references;

import java.util.LinkedList;
import java.util.List;

public class CellProcessorProviderHelper {
	private List<CellProcessorProvider> providers = new LinkedList<>();

	public CellProcessorProviderHelper() {
		providers.add(new SpecialProcessorProvider());
		providers.add(new EmptyReferenceProcessorProvider());
		providers.add(new VariablesReferenceProcessorProvider());
		providers.add(new EnvironmentPropertyProcessorProvider());
		providers.add(new DateReferenceProcessorProvider());
		providers.add(new FileFixtureReferenceProcessorProvider());
		providers.add(new PropertyReferenceProcessorProvider());
	}

	public List<CellProcessorProvider> getProviders() {
		return providers;
	}

	public void register(CellProcessorProvider provider) {
		providers.add(provider);
	}
}
