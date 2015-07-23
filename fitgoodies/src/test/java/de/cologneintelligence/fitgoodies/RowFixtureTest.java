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
package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author jwierum
 */
public final class RowFixtureTest extends FitGoodiesTestCase {

	public static class BusinessObject1 {
		public Integer x;
		public String y;
		public Integer z;

		public BusinessObject1(
				final Integer xVal,
				final String yVal,
				final Integer zVal) {
			x = xVal;
			y = yVal;
			z = zVal;
		}
	}

	public static class BusinessObject2 {
		private String[] strs;
		private String str;
		private String str2;

		public BusinessObject2(String str, String str2) {
			this.str = str;
			this.str2 = str2;
		}

		public BusinessObject2(String[] strs) {
			this.strs = strs;
		}

		public String[] getStrings() {
			return strs;
		}

		public String getString1() {
			return str;
		}

		public String getString2() {
			return str2;
		}
	}

	private class TestRowFixture2 extends RowFixture {

		private Object[] result;

		private TestRowFixture2() {
			this(new Object[0]);
		}

		private TestRowFixture2(Object[] result) {
			this.result = result;
		}

		public Object[] query() {
			return result;
		}

		public Class getTargetClass() {
			return BusinessObject2.class;
		}
	}

	private static class TestRowFixture1 extends RowFixture {

		@Override
		public Class<?> getTargetClass() {
			return BusinessObject1.class;
		}

		@Override
		public Object[] query() throws Exception {
			return new BusinessObject1[]{
					new BusinessObject1(1, "x", 3),
					new BusinessObject1(8, "matched", 6)
			};
		}
	}

	private static class TestRowFixture3 extends RowFixture {

		@Override
		public Class<?> getTargetClass() {
			return BusinessObject1.class;
		}

		@Override
		public Object[] query() throws Exception {
			return new BusinessObject1[]{
					new BusinessObject1(2, "x", 3),
					new BusinessObject1(5, "x", 6)
			};
		}
	}


	@Test
	public void testNumberCases() {
		TestRowFixture1 rowFixture = new TestRowFixture1();

		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("1", "x", "3"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().right, is(equalTo((Object) 3)));
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 1)));

		table = parseTable(
				tr("x", "y", "z"),
				tr("1", "match", "3"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 4)));
		assertThat(rowFixture.counts().right, is(equalTo((Object) 5)));
	}

	@Test
	public void testCrossReferencesForStringValues() {
		TestRowFixture1 rowFixture = new TestRowFixture1();

		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("8", "${2}", "6"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("2"));

		rowFixture.doTable(table);
		assertThat(rowFixture.counts().right, is(equalTo((Object) 3)));

		table = parseTable(
				tr("x", "y"),
				tr("8", "${test}"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 4)));
	}

	@Test
	public void testCrossReferencesForIntegerValues() {
		TestRowFixture3 sameValueRowFixture = new TestRowFixture3();
		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("${test}", "x", "3"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("test", "2"));

		sameValueRowFixture.doTable(table);
		assertThat(sameValueRowFixture.counts().right, is(3));
	}

	@Test
	public void testCrossReferencesExpectedRowsCountEqualsComputedRowsCount() {
		TestRowFixture3 sameValueRowFixture = new TestRowFixture3();

		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("${test2}", "x", "3"),
				tr("5", "x", "6"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("test2", "2"));

		sameValueRowFixture.doTable(table);
		assertThat(sameValueRowFixture.counts().right, is(6));
	}

	@Test
	public void testGetParams() {
		TestRowFixture1 rowFixture = new TestRowFixture1();

		rowFixture.setParams(new String[]{"x=y", "a=b"});

		assertThat(rowFixture.getParam("x"), is(equalTo("y")));
		assertThat(rowFixture.getParam("y"), is(nullValue()));

		assertThat(rowFixture.getParam("a", "z"), is(equalTo("b")));
		assertThat(rowFixture.getParam("u", "z"), is(equalTo("z")));
	}

	@Test
	public void testMatch() throws Exception {

        /*
        Now back to the bug I found: The problem stems from the fact
        that java doesn't do deep equality for arrays. Little known to
        me (I forget easily ;-), java arrays are equal only if they
        are identical. Unfortunately the 2 sort methods returns a map
        that is directly keyed on the value of the column without
        considering this little fact. Conclusion there is a missing
        and a surplus row where there should be one right row.
        -- Jacques Morel
        */

		RowFixture fixture = new TestRowFixture2();
		TypeAdapter arrayAdapter = TypeAdapter.on(fixture, fixture,
				BusinessObject2.class.getMethod("getStrings", new Class[0]));
		fixture.columnBindings = new TypeAdapter[]{arrayAdapter};

		List<Object> computed = new LinkedList<>();
		computed.add(new BusinessObject2(new String[]{"1"}));
		List<Parse> expected = new LinkedList<>();
		expected.add(parseTr("1"));
		fixture.match(expected, computed, 0);

		assertThat("right", fixture.counts().right, is(1));
		assertThat("exceptions", fixture.counts().exceptions, is(0));
		assertThat("missing", fixture.missing.size(), is(0));
		assertThat("surplus", fixture.surplus.size(), is(0));
	}

	@Test
	public void testMismatch() throws NoSuchMethodException {
		List<Object> computed = new LinkedList<>();
		computed.add(new BusinessObject2("a", "1"));
		computed.add(new BusinessObject2("b", "2"));
		computed.add(new BusinessObject2("c", "3"));
		computed.add(new BusinessObject2("d", "4"));
		computed.add(new BusinessObject2("e", "5"));

		RowFixture fixture = new TestRowFixture2(computed.toArray());

		Parse table = parseTable(tr("getString1()", "getString2()"), tr("a", "1"),
				tr("b", "2"), tr("d", "5"), tr("f", "7"));

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 5, 4, 0, 0);
		assertThat(table.at(0, 2, 0).body, is(equalTo("a")));
		assertThat(table.at(0, 2, 1).body, is(equalTo("1")));
		assertThat(table.at(0, 4, 0).body, is(equalTo("d")));
		assertThat(table.at(0, 4, 1).body, allOf(containsString("4"), containsString("5"),
				containsString("expected"), containsString("actual")));
		assertThat(table.at(0, 5, 0).body, allOf(startsWith("f"), containsString("missing")));
		assertThat(table.at(0, 6, 0).body, allOf(startsWith("e"), containsString("surplus")));
	}

}
