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

package de.cologneintelligence.fitgoodies.types;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.Matcher;
import org.junit.Test;

import static de.cologneintelligence.fitgoodies.types.InternalTestableString.TestType;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class InternalTestableStringTest extends FitGoodiesTestCase {
	@Test
	public void testEquals() {
		assertMatches("my content", "my content", TestType.EQUALS);
		assertMatchesNot("my content", "other content", TestType.EQUALS);
		assertMatchesNot("my content", "MY content", TestType.EQUALS);
		assertMatches("my content", "my content", TestType.EQUALSIC);
		assertMatches("my content", "MY content", TestType.EQUALSIC);
		assertMatchesNot("my content", "other content", TestType.EQUALSIC);
	}

	@Test
	public void testContains() {
		assertMatches("content", "my content contains...", TestType.CONTAINS);
		assertMatchesNot("my content", "this contains not", TestType.CONTAINS);
		assertMatchesNot("content", "my CONTENT contains...", TestType.CONTAINS);
		assertMatches("content", "my content contains...", TestType.CONTAINSIC);
		assertMatches("content", "my CONTENT contains...", TestType.CONTAINSIC);
		assertMatchesNot("my content", "this contains not", TestType.CONTAINSIC);
	}

	@Test
	public void testStartsWith() {
		assertMatches("a s", "a string", TestType.STARTSWITH);
		assertMatches("what", "what a test", TestType.STARTSWITH);
		assertMatchesNot("test", "what a test", TestType.STARTSWITH);
		assertMatchesNot("WHAT", "what a test", TestType.STARTSWITH);
		assertMatches("a s", "A string", TestType.STARTSWITHIC);
		assertMatches("WHAT", "what a test", TestType.STARTSWITHIC);
		assertMatchesNot("test", "what a test", TestType.STARTSWITHIC);
	}

	@Test
	public void testEndsWith() {
		assertMatches("string", "a string", TestType.ENDSWITH);
		assertMatches("test", "what a test", TestType.ENDSWITH);
		assertMatchesNot("what", "what a test", TestType.ENDSWITH);
		assertMatchesNot("TEST", "what a test", TestType.ENDSWITH);
		assertMatches("String", "A string", TestType.ENDSWITHIC);
		assertMatchesNot("what", "what a test", TestType.ENDSWITHIC);
	}

	@Test
	public void testRegex() {
		assertMatches("[ab]\\s+string", "a string", TestType.REGEX);
		assertMatches("test$", "what a test", TestType.REGEX);
		assertMatchesNot("what$", "what a test", TestType.REGEX);
		assertMatches("TEST", "what a test", TestType.REGEXIC);
		assertMatches("Stri?ng", "A string", TestType.REGEXIC);
		assertMatchesNot("what$", "what a test", TestType.REGEXIC);
	}

	@Test
	public void constructByName() {
		InternalTestableString internalTestableString = new InternalTestableString("string", "equals");
		assertThat(internalTestableString, (Matcher) is(equalTo(new TestableString("string"))));
		assertThat(internalTestableString, (Matcher) is(not(equalTo(new TestableString("a string")))));

		internalTestableString = new InternalTestableString("string", "CONTAINS");
		assertThat(internalTestableString, (Matcher) is(equalTo(new TestableString("string"))));
		assertThat(internalTestableString, (Matcher) is(equalTo(new TestableString("a string"))));

		internalTestableString = new InternalTestableString("string", "CONTAINS ic");
		assertThat(internalTestableString, (Matcher) is(equalTo(new TestableString("a STRing"))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructByNameThrowsError() {
		new InternalTestableString("string", "unknown");
	}

	@Test
	public void functionIsNullSave() {
		assertMatchesNot("test", null, TestType.EQUALS);
		assertMatchesNot(null, "test", TestType.EQUALS);
		assertMatches(null, null, TestType.EQUALS);
		assertThat(new InternalTestableString(null, (String) null), is(equalTo(null)));
	}

	@Test
	public void otherTypesUseToString() {
		assertMatches("test", new StringBuilder("test"), TestType.EQUALS);
		assertMatchesNot("test", new StringBuilder("test2"), TestType.EQUALS);
	}

	@Test
	public void arraysAreDecomposed() {
		assertMatchesNot("test2", new Object[]{new StringBuilder("test"), "test"}, TestType.EQUALS);
		assertMatchesNot("test", new Object[]{2}, TestType.EQUALS);

		assertMatches("test", new Object[]{new StringBuilder("test"), "test2"}, TestType.EQUALS);
		assertMatches("test", new Object[]{new StringBuilder("test"), new TestableString("test2")}, TestType.EQUALS);
		assertMatches("4", new int[]{1, 4, 5}, TestType.EQUALS);
	}

	@Test
	public void equalsIsDefault() {
		assertThat(new InternalTestableString("", (String) null).getTestType(), is(equalTo(TestType.EQUALS)));
	}

	private void assertMatches(String testContent, Object rhs, TestType testType) {
		InternalTestableString internalTestableString = new InternalTestableString(testContent, testType);
		assertThat(internalTestableString, (Matcher) is(equalTo(new TestableString(rhs))));
	}

	private void assertMatchesNot(String testContent, Object rhs, TestType testType) {
		InternalTestableString internalTestableString = new InternalTestableString(testContent, testType);
		assertThat(internalTestableString, (Matcher) is(not(equalTo(new TestableString(rhs)))));
	}
}
