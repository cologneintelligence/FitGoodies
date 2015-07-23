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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ActionFixtureTest extends FitGoodiesTestCase {

	@SuppressWarnings("unused")
	public static class TestFixture1 {
		boolean pressed = false;

		public String string1;
		public int int1;

		public boolean check1() {
			return true;
		}

		public int check2() {
			return 23;
		}

		public String check3() {
			return "none";
		}

		public void pressMethod() {
			pressed = true;
		}

		public void s1() {}

		public void s1(String s) {
			string1 = s;
		}

		public void s2(int i) {
			int1 = i;
		}
	}

	@SuppressWarnings("unused")
	public static class TestFixture2 {
		public void method(int x) {
		}

		public void method(String s) {
		}
	}

	private String start(String clazz) {
		return tr("start", clazz);
	}

	private String press(String method) {
		return tr("press", method);
	}

	private String enter(String method, String arg) {
		return tr("enter", method, arg);
	}

	private String check(String name, String expected) {
		return tr("check", name, expected);
	}

	@Test
	public void testStart() throws Exception {
		final String clazz = TestFixture1.class.getName();
		Parse parse = parseTable(start(clazz));

		final ActionFixture fixture = new ActionFixture();
		assertThat(fixture.actor, is(nullValue()));
		fixture.doTable(parse);
		assertCounts(fixture.counts(), parse, 0, 0, 0, 0);
		assertThat(fixture.actor, instanceOf(TestFixture1.class));
	}

	@Test
	public void testEnter() throws Exception {
		final String clazz = TestFixture1.class.getName();
		Parse parse = parseTable(start(clazz), enter("s1", "hello"), enter("s2", "42"));

		final ActionFixture fixture = new ActionFixture();
		fixture.doTable(parse);
		final TestFixture1 actor = (TestFixture1) fixture.actor;

		assertCounts(fixture.counts(), parse, 0, 0, 0, 0);
		assertThat(actor.int1, is(42));
		assertThat(actor.string1, is("hello"));
		assertThat(actor.pressed, is(false));
	}

	@Test
	public void testEnterRequiresSingleMethod() throws Exception {
		final String clazz = TestFixture2.class.getName();
		Parse parse = parseTable(start(clazz), enter("method", "hello"));

		final ActionFixture fixture = new ActionFixture();
		fixture.doTable(parse);
		assertCounts(fixture.counts(), parse, 0, 0, 0, 1);
	}

	@Test
	public void testPress() throws Exception {
		final String clazz = TestFixture1.class.getName();
		Parse parse = parseTable(start(clazz), press("pressMethod"));

		final ActionFixture fixture = new ActionFixture();
		fixture.doTable(parse);
		final TestFixture1 actor = (TestFixture1) fixture.actor;

		assertCounts(fixture.counts(), parse, 0, 0, 0, 0);
		assertThat(actor.pressed, is(true));
	}

	@Test
	public void testPressWrongMethod() throws Exception {
		final String clazz = TestFixture1.class.getName();
		Parse parse = parseTable(start(clazz), press("error"));

		final ActionFixture fixture = new ActionFixture();
		fixture.doTable(parse);
		final TestFixture1 actor = (TestFixture1) fixture.actor;

		assertCounts(fixture.counts(), parse, 0, 0, 0, 1);
		assertThat(actor.pressed, is(false));
	}

	@Test
	public void testCheck() throws Exception {
		final String clazz = TestFixture1.class.getName();
		Parse parse = parseTable(start(clazz), check("check1", "true"),
				check("check2", "23"), check("check3", "oops"));

		final ActionFixture fixture = new ActionFixture();
		fixture.doTable(parse);

		assertCounts(fixture.counts(), parse, 2, 1, 0, 0);
	}

}
