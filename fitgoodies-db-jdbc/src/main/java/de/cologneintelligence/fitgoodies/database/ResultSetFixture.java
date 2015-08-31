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


package de.cologneintelligence.fitgoodies.database;

import de.cologneintelligence.fitgoodies.RowFixture;
import de.cologneintelligence.fitgoodies.database.dynamic.ResultSetWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is a extended version of a row fixture, which takes a {@code ResultSet}
 * object and compares it with the given table. Therefore it creates a new,
 * temporary class which wraps the {@code ResultSet} using a
 * {@link de.cologneintelligence.fitgoodies.database.dynamic.DynamicObjectFactory} and fills these objects
 * with the individual rows of the {@code ResultSet}.
 */
public class ResultSetFixture extends RowFixture {
	private ResultSetWrapper table;

	/**
	 * Sets the ResultSet which is compared with the input table.
	 *
	 * @param resultSet {@code ResultSet} to use
	 * @throws SQLException Exception thrown by the {@code ResultSet}. You can propagate
	 *                      it to fit.
	 */
	public void setResultSet(final ResultSet resultSet) throws SQLException {
		table = new ResultSetWrapper(resultSet);
	}

	/**
	 * Gets the type of the dynamic created target class.
	 *
	 * @return the type of the target class.
	 */
	@Override
	public final Class<?> getTargetClass() {
		return table.getClazz();
	}

	/**
	 * Gets an array which represents the ResultSet as an object array.
	 * The type of these objects can be determined via {@code getTargetClass()}.
	 *
	 * @throws java.lang.Exception Exception thrown while generated actual results.
	 */
	@Override
	public final Object[] query() throws Exception {
		return table.getRows();
	}
}
