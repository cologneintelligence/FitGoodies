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


package de.cologneintelligence.fitgoodies.adapters;

import fit.TypeAdapter;

/**
 * The {@code AbstractTypeAdapter} is used to implement a custom
 * {@code TypeAdapter}. This adapter is called with another instance of
 * {@code TypeAdapter} which normally is responsible for
 * {@code Object}. The resulting adapter should be responsible only for
 * the generic type T.
 * <p>
 *
 * It is recommended to override {@code toString}, {@code parse} and
 * {@code getType}.
 * <p>
 *
 * A {@code TypeAdapter} must be registered using
 * {@code {@link de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper#register(Class)}}
 * from code or using a {@code {@link de.cologneintelligence.fitgoodies.adapters.SetupFixture}}
 * from HTML.
 *
 * @param <T> type the {@code AbstractTypeAdapter} is responsible for.
 *
 */
public abstract class AbstractTypeAdapter<T> extends TypeAdapter {
	private final String parameter;

	/**
	 * Constructs an AbstractTypeAdapter object using {@code ta} as
	 * template.
	 *
	 * The values {@code field}, {@code fixture}, {@code method},
	 * {@code target} and {@code type} are copied.
	 *
	 * @param ta template <class>TypeAdapter</code> to copy.
	 * @param convertParameter parameter which is stored and can be used with
	 * 		{@code toString}/{@code parse} to customize the behavior.
	 */
	public AbstractTypeAdapter(final TypeAdapter ta, final String convertParameter) {
		this.field = ta.field;
		this.fixture = ta.fixture;
		this.method = ta.method;
		this.target = ta.target;
		this.type = ta.type;
		parameter = convertParameter;
	}

	/**
	 * Returns the type of which this class is responsible for.
	 * The return Value sould just be the type of T.
	 * @return the type of T.
	 */
	public abstract Class<T> getType();

	/**
	 * Returns the saved parameter.
	 * @return the saved parameter
	 */
	public final String getParameter() {
		return parameter;
	}
}
