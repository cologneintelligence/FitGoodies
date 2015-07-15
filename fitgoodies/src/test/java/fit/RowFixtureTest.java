// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

package fit;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RowFixtureTest {

	@SuppressWarnings("unused")
	class BusinessObject {
		private String[] strs;

		public BusinessObject(String[] strs) {
			this.strs = strs;
		}

		public String[] getStrings() {
			return strs;
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

		List<BusinessObject> computed = new LinkedList<>();
		computed.add(new BusinessObject(new String[]{"1"}));
		List<Parse> expected = new LinkedList<>();
		expected.add(new Parse("tr", "", new Parse("td", "1", null, null), null));
		fixture.match(expected, computed, 0);

		assertThat("right", fixture.counts.right, is(1));
		assertThat("exceptions", fixture.counts.exceptions, is(0));
		assertThat("missing", fixture.missing.size(), is(0));
		assertThat("surplus", fixture.surplus.size(), is(0));
	}

	private class TestRowFixture extends RowFixture {
		public Object[] query() throws Exception  // get rows to be compared
		{
			return new Object[0];
		}

		public Class getTargetClass()             // get expected type of row
		{
			return BusinessObject.class;
		}
	}
}
