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


package de.cologneintelligence.fitgoodies.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.cologneintelligence.fitgoodies.RowFixture;
import de.cologneintelligence.fitgoodies.dynamic.ResultSetWrapper;


/**
 * This class is a extended version of a row fixture, which takes a <code>ResultSet</code>
 * object and compares it with the given table. Therefore it creates a new,
 * temporary class which wraps the <code>ResultSet</code> using a
 * {@link de.cologneintelligence.fitgoodies.dynamic.DynamicObjectFactory} and fills these objects
 * with the individual rows of the <code>ResultSet</code>.
 *
 */
public class ResultSetFixture extends RowFixture {
	private ResultSetWrapper table;

	/**
	 * Sets the ResultSet which is compared with the input table.
	 * @param resultSet <code>ResultSet</code> to use
	 * @throws SQLException Exception thrown by the <code>ResultSet</code>. You can propagate
	 * 		it to fit.
	 */
	public void setResultSet(final ResultSet resultSet) throws SQLException {
		table = new ResultSetWrapper(resultSet);
	}

	/**
	 * Gets the type of the dynamic created target class.
	 * @return the type of the target class.
	 */
	@Override
	public final Class<?> getTargetClass() {
		return table.getClazz();
	}

	/**
	 * Gets an array which represents the ResultSet as an object array.
	 * The type of these objects can be determined via <code>getTargetClass()</code>.
         * @return
         * @throws java.lang.Exception
	 */
	@Override
	public final Object[] query() throws Exception {
		return table.getRows();
	}
}
