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


package fitgoodies.dynamic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jmock.Expectations;

import fitgoodies.FitGoodiesTestCase;
import fitgoodies.ScientificDouble;

/**
 * $Id: ResultSetWrapperTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class ResultSetWrapperTest extends FitGoodiesTestCase {
	public final void testGetTypesWithOneRow() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(3));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("name"));
			oneOf(meta).getColumnName(3); will(returnValue("surname"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(Integer.valueOf(42)));
			oneOf(rs).getObject(2); will(returnValue("Angela"));
			oneOf(rs).getObject(3); will(returnValue("Bennett"));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertEquals(3, clazz.getFields().length);
		assertEquals(Integer.class, clazz.getField("id").getType());
		assertEquals(String.class, clazz.getField("name").getType());
		assertEquals(String.class, clazz.getField("surname").getType());
	}

	public final void testGetTypesWithThreeRows() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(2));
			oneOf(meta).getColumnName(1); will(returnValue("name"));
			oneOf(meta).getColumnName(2); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(null));
			oneOf(rs).getObject(2); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(new StringBuilder("Angela")));
			oneOf(rs).getObject(2); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(new StringBuilder("Anika")));
			oneOf(rs).getObject(2); will(returnValue(new BigInteger("31")));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertEquals(2, clazz.getFields().length);
		assertEquals(StringBuilder.class, clazz.getField("name").getType());
		assertEquals(BigInteger.class, clazz.getField("age").getType());
	}

	public final void testGetTypesWithIncompleteRows() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(2));
			oneOf(meta).getColumnName(1); will(returnValue("name"));
			oneOf(meta).getColumnName(2); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(null));
			oneOf(rs).getObject(2); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(new StringBuilder("Angela")));
			oneOf(rs).getObject(2); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(new StringBuilder("Anika")));
			oneOf(rs).getObject(2); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Class<?> clazz = reader.getClazz();

		assertEquals(2, clazz.getFields().length);
		assertEquals(StringBuilder.class, clazz.getField("name").getType());
		assertEquals(Object.class, clazz.getField("age").getType());
	}

	public final void testCreateObjectOne() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(2));
			oneOf(meta).getColumnName(1); will(returnValue("name"));
			oneOf(meta).getColumnName(2); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(new StringBuilder("Anika")));
			oneOf(rs).getObject(2); will(returnValue(new BigInteger("31")));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);

		Object o = reader.createContainerObject();
		assertEquals(StringBuilder.class, o.getClass().getField("name").getType());
		assertEquals(BigInteger.class, o.getClass().getField("age").getType());
	}

	public final void testCreateObjectTwo() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(4));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("fullname"));
			oneOf(meta).getColumnName(3); will(returnValue("age"));
			oneOf(meta).getColumnName(4); will(returnValue("problem"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(42));
			oneOf(rs).getObject(2); will(returnValue(new StringBuffer("Angela Bennett")));
			oneOf(rs).getObject(3); will(returnValue(new BigDecimal("31")));
			oneOf(rs).getObject(4); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);

		Object o = reader.createContainerObject();
		assertEquals(StringBuffer.class, o.getClass().getField("fullname").getType());
		assertEquals(BigDecimal.class, o.getClass().getField("age").getType());
		assertEquals(Integer.class, o.getClass().getField("id").getType());
		assertEquals(Object.class, o.getClass().getField("problem").getType());
	}

	public final void testGetObject() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(3));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("fullname"));
			oneOf(meta).getColumnName(3); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(42));
			oneOf(rs).getObject(2); will(returnValue(new StringBuffer("Angela Bennett")));
			oneOf(rs).getObject(3); will(returnValue(new BigDecimal("31")));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(23));
			oneOf(rs).getObject(2); will(returnValue(new StringBuffer("Anika Hanson")));
			oneOf(rs).getObject(3); will(returnValue(new BigDecimal("32")));

			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Angela Bennett";
		Object actual = o[0].getClass().getField("fullname").get(o[0]).toString();
		assertEquals(expected, actual);

		expected = new BigDecimal(31);
		actual = o[0].getClass().getField("age").get(o[0]);
		assertEquals(expected, actual);

		expected = 23;
		actual = o[1].getClass().getField("id").get(o[1]);
		assertEquals(expected, actual);

		expected = new BigDecimal(32);
		actual = o[1].getClass().getField("age").get(o[1]);
		assertEquals(expected, actual);
	}

	public final void testGetObjectWithIncompleteData() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(3));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("name"));
			oneOf(meta).getColumnName(3); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(1));
			oneOf(rs).getObject(2); will(returnValue(new StringBuilder("Anika Hanson")));
			oneOf(rs).getObject(3); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(2));
			oneOf(rs).getObject(2); will(returnValue(new StringBuilder("Angela Bennett")));
			oneOf(rs).getObject(3); will(returnValue(new BigDecimal("25")));

			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Anika Hanson";
		Object actual = o[0].getClass().getField("name").get(o[0]).toString();
		assertEquals(expected, actual);

		expected = new BigDecimal(25);
		actual =  o[1].getClass().getField("age").get(o[1]);
		assertEquals(expected, actual);

		expected = 1;
		actual = o[0].getClass().getField("id").get(o[0]);
		assertEquals(expected, actual);

		expected = 2;
		actual = o[1].getClass().getField("id").get(o[1]);
		assertEquals(expected, actual);
	}

	public final void testGetObjectWithMissingColumn() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(3));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("name"));
			oneOf(meta).getColumnName(3); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(1));
			oneOf(rs).getObject(2); will(returnValue(new StringBuilder("Anika Hanson")));
			oneOf(rs).getObject(3); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(2));
			oneOf(rs).getObject(2); will(returnValue(new StringBuilder("Angela Bennett")));
			oneOf(rs).getObject(3); will(returnValue(null));

			oneOf(rs).next(); will(returnValue(false));
			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		Object expected = "Anika Hanson";
		Object actual = o[0].getClass().getField("name").get(o[0]).toString();
		assertEquals(expected, actual);

		expected = null;
		actual = o[1].getClass().getField("age").get(o[1]);
		assertEquals(expected, actual);
	}

	public final void testGetObjectWithEmptyTable() throws SQLException {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(3));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("name"));
			oneOf(meta).getColumnName(3); will(returnValue("age"));

			oneOf(rs).next(); will(returnValue(false));
			oneOf(rs).next(); will(returnValue(false));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object[] o = reader.getRows();

		assertEquals(0, o.length);
	}

	public final void testCreateObjectWithFloat() throws Exception {
		final ResultSet rs = mock(ResultSet.class);
		final ResultSetMetaData meta = mock(ResultSetMetaData.class);

		checking(new Expectations() {{
			oneOf(rs).getMetaData(); will(returnValue(meta));

			oneOf(meta).getColumnCount(); will(returnValue(2));
			oneOf(meta).getColumnName(1); will(returnValue("id"));
			oneOf(meta).getColumnName(2); will(returnValue("value"));

			oneOf(rs).next(); will(returnValue(true));
			oneOf(rs).getObject(1); will(returnValue(2));
			oneOf(rs).getObject(2); will(returnValue(Float.valueOf(2.5f)));
		}});

		ResultSetWrapper reader = new ResultSetWrapper(rs);
		Object o = reader.createContainerObject();
		assertEquals(ScientificDouble.class, o.getClass().getField("value").getType());
	}
}
