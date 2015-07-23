/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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


package com.mycompany.bookstore.fittests;


import com.mycompany.bookstore.ISBN;

import de.cologneintelligence.fitgoodies.TypeAdapter;
import fitgoodies.adapters.AbstractTypeAdapter;

public final class ISBNTypeAdapter extends AbstractTypeAdapter<ISBN> {
	public ISBNTypeAdapter(final TypeAdapter ta, final String parameter) {
		super(ta, parameter);
	}

	@Override
	public Object parse(final String s) throws Exception {
		return new ISBN(s);
	}

	@Override
	public Class<ISBN> getType() {
		return ISBN.class;
	}

	@Override
	public String toString(final Object o) {
		return ((ISBN) o).stripped();
	}
}
