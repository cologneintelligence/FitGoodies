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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimedActionFixtureTest extends FitGoodiesFixtureTestCase<TimedActionFixtureTest.TimedFixture> {

    public static class ActionTestFixture {
		public boolean called;

		public void call() {
			called = true;
		}
	}

	public static class TimedFixture extends TimedActionFixture {
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

    @Override
    protected Class<TimedFixture> getFixtureClass() {
        return TimedFixture.class;
    }

	@Test
	public void testFixtureFast() {
        useTable(tr("start", ActionTestFixture.class.getName()),
            tr("press", "call"));

		int startTime = 5 * 1000;
		fixture.date1 = new Date(startTime);
		fixture.date2 = new Date(startTime + 300);

        run();

		assertThat(((ActionTestFixture) fixture.actor).called, is(true));

		assertThat(htmlAt(0, 2), containsAll("00:00:05", "time"));
		assertThat(htmlAt(0, 3), containsAll("&nbsp;", "split"));
		assertThat(htmlAt(1, 2), containsAll("00:00:05", "time"));
		assertThat(htmlAt(1, 3), containsAll("&nbsp;", "split"));
	}

	@Test
	public void testFixtureSlow() {
		useTable(tr("start", ActionTestFixture.class.getName()),
            tr("press", "call"));

		final int startTime = 1000;
		fixture.date1 = new Date(startTime);
		fixture.date2 = new Date(startTime + 7501);

        run();

		assertThat(((ActionTestFixture) fixture.actor).called, is(true));
		assertThat(htmlAt(0, 2), containsAll("00:00:01", "time"));
		assertThat(htmlAt(0, 3), containsAll("7.501", "split"));
		assertThat(htmlAt(1, 2), containsAll("00:00:08", "time"));
		assertThat(htmlAt(1, 3), containsAll("&nbsp;", "split"));
	}

}
