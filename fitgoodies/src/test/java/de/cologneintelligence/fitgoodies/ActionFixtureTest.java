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

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.WaitForResult;
import org.hamcrest.Matcher;
import org.junit.Test;

import static de.cologneintelligence.fitgoodies.test.MethodMatcher.aMethodNamed;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ActionFixtureTest extends FitGoodiesFixtureTestCase<ActionFixtureTest.TestActionFixture> {

	@SuppressWarnings("unused")
	public static class TestActionFixture extends ActionFixture {
		public boolean arg1;
		public int arg2;

		public TestActionFixture() {
			super(mock(WaitForResult.class));
		}

		public void func1() throws Exception {
			transformAndEnter();
		}

		public void func2() throws Exception {
			transformAndEnter();
		}

		public void func1(Boolean value) {
			arg1 = value;
		}

		public void func2(Integer value) {
			arg2 = value;
		}
	}

	@SuppressWarnings("unused")
	public static class TargetObject {
		boolean pressed = false;

		public String string1;
		public int int1;

		public boolean check1() {
			return true;
		}

		public int check2() {
			return 23;
		}

		public void pressMethod() {
			pressed = true;
		}

		public void s1() {
		}

		public void s1(String s) {
			string1 = s;
		}

		public void s2(Integer i) {
			int1 = i;
		}

		public void method(int x) {
		}

		public void method(String s) {
		}

		public boolean waitForMe() {
			return true;
		}
	}

	@Override
	protected Class<TestActionFixture> getFixtureClass() {
		return TestActionFixture.class;
	}

	private static final String CLAZZ = TargetObject.class.getName();

	private String start() {
		return tr("start", CLAZZ);
	}

	private String press(String method) {
		return tr("press", method);
	}

	private String enter(String method, String arg) {
		return tr("enter", method, arg);
	}

	private String wait(String method, String maxTime, String sleepTime) {
		return tr("waitFor", method, maxTime, sleepTime);
	}

	@Test
	public void testStart() throws Exception {
		useTable(start());

		assertThat(fixture.actor, (Matcher) is(sameInstance(fixture)));
		run();

		assertCounts(0, 0, 0, 0);
		assertThat(fixture.actor, instanceOf(TargetObject.class));
	}

	@Test
	public void testEnter() throws Exception {
		useTable(start(), enter("s1", "hello"), tr("enter[" + "arg" + "]", "s2", "42"));

		prepareTransformation("hello", "parsed value", null);
		prepareTransformation("42", 23, "arg");

		run();

		assertCounts(0, 0, 0, 0);

		TargetObject actor = (TargetObject) fixture.actor;
		assertThat(actor.int1, is(23));
		assertThat(actor.string1, is("parsed value"));
	}

	@Test
	public void testEnterRequiresSingleMethod() throws Exception {
		useTable(start(), enter("method", "hello"));

		run();
		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void testPress() throws Exception {
		useTable(start(), press("pressMethod"));

		run();

		TargetObject actor = (TargetObject) fixture.actor;
		assertCounts(0, 0, 0, 0);
		assertThat(actor.pressed, is(true));
	}

	@Test
	public void testPressWrongMethod() throws Exception {
		useTable(start(), press("error"));

		run();
		final TargetObject actor = (TargetObject) fixture.actor;

		assertCounts(0, 0, 0, 1);
		assertThat(actor.pressed, is(false));
	}

	@Test
	public void testCheck() throws Exception {
		useTable(start(), tr("check", "check1", "true"),
				tr("check", "check2", "23"));

		expectMethodValidation(1, 2, "check1");
		expectMethodValidation(2, 2, "check2");

		run();
	}

	@Test
	public void transformAndEnterIsShortCut() throws Exception {
		useTable(start(),
				tr("func1", "true"),
				tr("func2", "23"));

		prepareTransformation("true", true, null);
		prepareTransformation("23", 42, null);

		run();

		assertCounts(0, 0, 0, 0);

		assertThat(fixture.arg1, is(true));
		assertThat(fixture.arg2, is(42));
	}

	@Test
	public void waitForTrueWithDelay() throws Exception {
		String methodName = "waitForMe";
		String maxTime = "100";
		String sleepTime = "10";
		long parsedMaxTime = 200L;
		long parsedSleepTime = 20L;

		useTable(start(), wait(methodName, maxTime, sleepTime));

		when(fixture.waitForResult.lastCallWasSuccessful()).thenReturn(true);
		when(fixture.waitForResult.getLastElapsedTime()).thenReturn(50L);

		prepareTransformation(maxTime, parsedMaxTime, null);
		prepareTransformation(sleepTime, parsedSleepTime, null);

		run();

		assertCounts(1, 0, 0, 0);
		assertThat(htmlAt(1, 2), containsString("50"));

		verify(fixture.waitForResult).wait(
				argThatSame(fixture.actor),
				argThat(aMethodNamed(methodName)),
				longThat(is(parsedMaxTime)),
				longThat(is(parsedSleepTime)));
	}

	@Test
	public void waitForTrueWithDelayWithoutExplicitSleep() throws Exception {
		String methodName = "waitForMe";

		String maxTime = "1000";
		long parsedMaxTime = 500L;

		useTable(start(), tr("waitFor", methodName, maxTime));

		when(fixture.waitForResult.lastCallWasSuccessful()).thenReturn(true);
		when(fixture.waitForResult.getLastElapsedTime()).thenReturn(10L);

		prepareTransformation(maxTime, parsedMaxTime, null);

		run();

		assertCounts(1, 0, 0, 0);
		assertThat(htmlAt(1, 2), containsString("10"));

		verify(fixture.waitForResult).wait(
				argThatSame(fixture.actor),
				argThat(aMethodNamed(methodName)),
				longThat(is(parsedMaxTime)),
				longThat(is(ActionFixture.DEFAULT_SLEEP_TIME)));
	}

	@Test
	public void waitForTrueFails() throws Exception {
		String methodName = "waitForMe";

		String maxTime = "1000";
		long parsedMaxTime = 500L;

		useTable(start(), tr("waitFor", methodName, maxTime));

		when(fixture.waitForResult.lastCallWasSuccessful()).thenReturn(false);

		prepareTransformation(maxTime, parsedMaxTime, null);

		run();

		assertCounts(0, 1, 0, 0);
		assertThat(htmlAt(1, 2), containsString("Timeout"));

		verify(fixture.waitForResult).wait(
				argThatSame(fixture.actor),
				argThat(aMethodNamed(methodName)),
				longThat(is(parsedMaxTime)),
				longThat(is(ActionFixture.DEFAULT_SLEEP_TIME)));
	}

	@Test
	public void defaultConstructorUsesRealWaitFor() {
		assertThat(new ActionFixture().waitForResult, is(instanceOf(WaitForResult.class)));
	}

	protected void prepareTransformation(String input, Object result, String parameter) throws java.text.ParseException {
		@SuppressWarnings("RedundantStringConstructorCall")
		String s = new String();

		preparePreprocess(input, s);
		TypeHandler handler = prepareGetTypeHandler(result.getClass(), parameter);
		when(handler.parse(argThatSame(s))).thenReturn(result);
	}
}
