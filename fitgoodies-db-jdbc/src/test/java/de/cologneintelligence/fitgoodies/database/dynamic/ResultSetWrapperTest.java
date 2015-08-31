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

package de.cologneintelligence.fitgoodies.database.dynamic;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.types.ScientificDouble;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ResultSetWrapperTest extends FitGoodiesTestCase {
	@Test
	public void testGetTypesWithOneRow() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);
		when(meta.getColumnCount()).thenReturn(3);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("name");
		when(meta.getColumnName(3)).thenReturn("surname");
		when(rs.next()).thenReturn(true);
		when(rs.getObject(1)).thenReturn(42);
		when(rs.getObject(2)).thenReturn("Angela");
		when(rs.getObject(3)).thenReturn("Bennett");

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertThat(clazz.getFields().length, is(equalTo((Object) 3)));
		assertThat(clazz.getField("id").getType(), (Matcher) is(equalTo(Integer.class)));
		assertThat(clazz.getField("name").getType(), (Matcher) is(equalTo(String.class)));
		assertThat(clazz.getField("surname").getType(), (Matcher) is(equalTo(String.class)));
	}

	@Test
	public void testGetTypesWithThreeRows() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnName(1)).thenReturn("name");
		when(meta.getColumnName(2)).thenReturn("age");

		when(rs.next()).thenReturn(true, true, true);
		when(rs.getObject(1)).thenReturn(null);
		when(rs.getObject(2)).thenReturn(null);

		when(rs.getObject(1)).thenReturn(new StringBuilder("Angela"));
		when(rs.getObject(2)).thenReturn(null);

		when(rs.getObject(1)).thenReturn(new StringBuilder("Anika"));
		when(rs.getObject(2)).thenReturn(new BigInteger("31"));

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertThat(clazz.getFields().length, is(equalTo((Object) 2)));
		assertThat(clazz.getField("name").getType(), (Matcher) is(equalTo(StringBuilder.class)));
		assertThat(clazz.getField("age").getType(), (Matcher) is(equalTo(BigInteger.class)));
	}

	@Test
	public void testGetTypesWithIncompleteRows() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnName(1)).thenReturn("name");
		when(meta.getColumnName(2)).thenReturn("age");

		when(rs.next()).thenReturn(true, true, true, false);

		when(rs.getObject(1)).thenReturn(null);
		when(rs.getObject(2)).thenReturn(null);

		when(rs.getObject(1)).thenReturn(new StringBuilder("Angela"));
		when(rs.getObject(2)).thenReturn(null);

		when(rs.getObject(1)).thenReturn(new StringBuilder("Anika"));
		when(rs.getObject(2)).thenReturn(null);

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertThat(clazz.getFields().length, is(equalTo((Object) 2)));
		assertThat(clazz.getField("name").getType(), (Matcher) is(equalTo(StringBuilder.class)));
		assertThat(clazz.getField("age").getType(), (Matcher) is(equalTo(Object.class)));
	}

	@Test
	public void testCreateObjectOne() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnName(1)).thenReturn("name");
		when(meta.getColumnName(2)).thenReturn("age");

		when(rs.next()).thenReturn(true);
		when(rs.getObject(1)).thenReturn(new StringBuilder("Anika"));
		when(rs.getObject(2)).thenReturn(new BigInteger("31"));

		ResultSetWrapper reader = new ResultSetWrapper(rs);

		Object o = reader.createContainerObject();
		assertThat(o.getClass().getField("name").getType(), (Matcher) is(equalTo(StringBuilder.class)));
		assertThat(o.getClass().getField("age").getType(), (Matcher) is(equalTo(BigInteger.class)));
	}

	@Test
	public void testCreateObjectTwo() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(4);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("fullname");
		when(meta.getColumnName(3)).thenReturn("age");
		when(meta.getColumnName(4)).thenReturn("problem");

		when(rs.next()).thenReturn(true, false);

		when(rs.getObject(1)).thenReturn(42);
		when(rs.getObject(2)).thenReturn(new StringBuffer("Angela Bennett"));
		when(rs.getObject(3)).thenReturn(new BigDecimal("31"));
		when(rs.getObject(4)).thenReturn(null);

		ResultSetWrapper reader = new ResultSetWrapper(rs);

		Object o = reader.createContainerObject();
		assertThat(o.getClass().getField("fullname").getType(), (Matcher) is(equalTo(StringBuffer.class)));
		assertThat(o.getClass().getField("age").getType(), (Matcher) is(equalTo(BigDecimal.class)));
		assertThat(o.getClass().getField("id").getType(), (Matcher) is(equalTo(Integer.class)));
		assertThat(o.getClass().getField("problem").getType(), (Matcher) is(equalTo(Object.class)));
	}

	@Test
	public void testGetObject() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(3);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("fullname");
		when(meta.getColumnName(3)).thenReturn("age");

		when(rs.next()).thenReturn(true, true, false);

		when(rs.getObject(1)).thenReturn(42, 23);
		when(rs.getObject(2)).thenReturn(new StringBuffer("Angela Bennett"), new StringBuffer("Anika Hanson"));
		when(rs.getObject(3)).thenReturn(new BigDecimal("31"), new BigDecimal("32"));

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Angela Bennett";
		Object actual = o[0].getClass().getField("fullname").get(o[0]).toString();
		assertThat(actual, is(equalTo(expected)));

		expected = new BigDecimal(31);
		actual = o[0].getClass().getField("age").get(o[0]);
		assertThat(actual, is(equalTo(expected)));

		expected = 23;
		actual = o[1].getClass().getField("id").get(o[1]);
		assertThat(actual, is(equalTo(expected)));

		expected = new BigDecimal(32);
		actual = o[1].getClass().getField("age").get(o[1]);
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void testGetObjectWithIncompleteData() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(3);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("name");
		when(meta.getColumnName(3)).thenReturn("age");

		when(rs.next()).thenReturn(true, true, false);

		when(rs.getObject(1)).thenReturn(1, 2);
		when(rs.getObject(2)).thenReturn(new StringBuilder("Anika Hanson"), new StringBuilder("Angela Bennett"));
		when(rs.getObject(3)).thenReturn(null, new BigDecimal("25"));

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Anika Hanson";
		Object actual = o[0].getClass().getField("name").get(o[0]).toString();
		assertThat(actual, is(equalTo(expected)));

		expected = new BigDecimal(25);
		actual = o[1].getClass().getField("age").get(o[1]);
		assertThat(actual, is(equalTo(expected)));

		expected = 1;
		actual = o[0].getClass().getField("id").get(o[0]);
		assertThat(actual, is(equalTo(expected)));

		expected = 2;
		actual = o[1].getClass().getField("id").get(o[1]);
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void testGetObjectWithMissingColumn() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(3);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("name");
		when(meta.getColumnName(3)).thenReturn("age");

		when(rs.next()).thenReturn(true, true, false);

		when(rs.getObject(1)).thenReturn(1, 2);
		when(rs.getObject(2)).thenReturn(new StringBuilder("Anika Hanson"), new StringBuilder("Angela Bennett"));
		when(rs.getObject(3)).thenReturn(null);

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Anika Hanson";
		Object actual = o[0].getClass().getField("name").get(o[0]).toString();
		assertThat(actual, is(equalTo(expected)));

		actual = o[1].getClass().getField("age").get(o[1]);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void testGetObjectWithEmptyTable() throws SQLException {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(3);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("name");
		when(meta.getColumnName(3)).thenReturn("age");

		when(rs.next()).thenReturn(false);

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		assertThat(o.length, is(equalTo((Object) 0)));
	}

	@Test
	public void testCreateObjectWithFloat() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rs.getMetaData()).thenReturn(meta);

		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("value");

		when(rs.next()).thenReturn(true);
		when(rs.getObject(1)).thenReturn(2);
		when(rs.getObject(2)).thenReturn(2.5);

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object o = reader.createContainerObject();
		assertThat(o.getClass().getField("value").getType(), (Matcher) is(equalTo(ScientificDouble.class)));
	}
}
