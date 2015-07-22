// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

package fit;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RowFixtureTest extends FitGoodiesTestCase {

	@SuppressWarnings("unused")
	class BusinessObject {
		private String[] strs;
		private String str;
		private String str2;

		public BusinessObject(String str, String str2) {
			this.str = str;
			this.str2 = str2;
		}

		public BusinessObject(String[] strs) {
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

		RowFixture fixture = new TestRowFixture();
		TypeAdapter arrayAdapter = TypeAdapter.on(fixture,
				BusinessObject.class.getMethod("getStrings", new Class[0]));
		fixture.columnBindings = new TypeAdapter[]{arrayAdapter};

		List<Object> computed = new LinkedList<>();
		computed.add(new BusinessObject(new String[]{"1"}));
		List<Parse> expected = new LinkedList<>();
		expected.add(parseTr("1"));
		fixture.match(expected, computed, 0);

		assertThat("right", fixture.counts.right, is(1));
		assertThat("exceptions", fixture.counts.exceptions, is(0));
		assertThat("missing", fixture.missing.size(), is(0));
		assertThat("surplus", fixture.surplus.size(), is(0));
	}

	@Test
	public void testMismatch() throws NoSuchMethodException {
		List<Object> computed = new LinkedList<>();
		computed.add(new BusinessObject("a", "1"));
		computed.add(new BusinessObject("b", "2"));
		computed.add(new BusinessObject("c", "3"));
		computed.add(new BusinessObject("d", "4"));
		computed.add(new BusinessObject("e", "5"));

		RowFixture fixture = new TestRowFixture(computed.toArray());

		Parse table = parseTable(tr("getString1()", "getString2()"), tr("a", "1"),
				tr("b", "2"), tr("d", "5"), tr("f", "7"));

		fixture.doTable(table);

		assertCounts(fixture.counts, table, 5, 4, 0, 0);
		assertThat(table.at(0, 2, 0).body, is(equalTo("a")));
		assertThat(table.at(0, 2, 1).body, is(equalTo("1")));
		assertThat(table.at(0, 4, 0).body, is(equalTo("d")));
		assertThat(table.at(0, 4, 1).body, allOf(containsString("4"), containsString("5"),
				containsString("expected"), containsString("actual")));
		assertThat(table.at(0, 5, 0).body, allOf(startsWith("f"), containsString("missing")));
		assertThat(table.at(0, 6, 0).body, allOf(startsWith("e"), containsString("surplus")));
	}

	private class TestRowFixture extends RowFixture {

		private Object[] result;

		private TestRowFixture() {
			this(new Object[0]);
		}

		private TestRowFixture(Object[] result) {
			this.result = result;
		}

		public Object[] query() throws Exception  // get rows to be compared
		{
			return result;
		}

		public Class getTargetClass()             // get expected type of row
		{
			return BusinessObject.class;
		}
	}
}
