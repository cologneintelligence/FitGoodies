/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

package fit;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimedActionFixtureTest extends FitGoodiesTestCase {

	public static class ActionTestFixture {
		public boolean called;

		public void call() {
			called = true;
		}
	}

	private static class TimedFixture extends TimedActionFixture {
		private boolean first = true;
		public Date date1;
		public Date date2;

		@Override
		public Date time() {
			Date date = first ? date1 : date2;
			first = false;
			return date;
		}
	}

	@Test
	public void testFixtureFast() {
		Parse table = parseTable(tr("start", ActionTestFixture.class.getName()),
				tr("press", "call"));

		final TimedFixture fixture = new TimedFixture();
		final int startTime = 5 * 1000;
		fixture.date1 = new Date(startTime);
		fixture.date2 = new Date(startTime + 300);
		fixture.doTable(table);

		assertThat(((ActionTestFixture) fixture.actor).called, is(true));

		assertThat(table.at(0, 0, 1).text(), is(equalTo("time")));
		assertThat(table.at(0, 0, 2).text(), is(equalTo("split")));
		assertThat(table.at(0, 1, 2).text(), is(equalTo("01:00:05")));
		assertThat(table.at(0, 1, 3).text(), is(equalTo("&nbsp;")));
		assertThat(table.at(0, 2, 2).text(), is(equalTo("01:00:05")));
		assertThat(table.at(0, 2, 3).text(), is(equalTo("&nbsp;")));
	}

	@Test
	public void testFixtureSlow() {
		Parse table = parseTable(tr("start", ActionTestFixture.class.getName()),
				tr("press", "call"));

		final TimedFixture fixture = new TimedFixture();
		final int startTime = 1000;
		fixture.date1 = new Date(startTime);
		fixture.date2 = new Date(startTime + 7501);
		fixture.doTable(table);

		assertThat(((ActionTestFixture) fixture.actor).called, is(true));
		assertThat(table.at(0, 0, 1).text(), is(equalTo("time")));
		assertThat(table.at(0, 0, 2).text(), is(equalTo("split")));
		assertThat(table.at(0, 1, 2).text(), is(equalTo("01:00:01")));
		assertThat(table.at(0, 1, 3).text(), is(equalTo("7.501")));
		assertThat(table.at(0, 2, 2).text(), is(equalTo("01:00:08")));
		assertThat(table.at(0, 2, 3).text(), is(equalTo("&nbsp;")));
	}

}
