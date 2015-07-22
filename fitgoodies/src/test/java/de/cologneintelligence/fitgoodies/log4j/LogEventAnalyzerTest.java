/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import fit.Counts;
import fit.Fixture;
import fit.Parse;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public final class LogEventAnalyzerTest extends FitGoodiesTestCase {
	private LoggingEvent[] list;

	@Before
	public void setUp() throws Exception {
		list = prepareCheckForGreenTest();
	}

	private LoggingEvent[] prepareCheckForGreenTest() {
		List<LoggingEvent> list = new LinkedList<>();

		list.add(new LoggingEvent("com.fqdn.class1", null,
				100, Level.ERROR, "a message", "thread1",
				new ThrowableInformation(new RuntimeException("xxx")),
				"ndc", null, null));
		list.add(new LoggingEvent("com.fqdn.class1", null, 120,
				Level.INFO, "no error", "thread2", null, "ndc", null, null));
		list.add(new LoggingEvent("rootLogger", null, 140, Level.DEBUG,
				"a root message", "main",
				new ThrowableInformation(new RuntimeException("yyy")),
				null, null, null));

		return list.toArray(new LoggingEvent[list.size()]);
	}

	@Test
	public void testParseContains() {
		final Fixture fixture = aFixture();
		final Parse cell1 = parseTd("a message");
		final Parse cell2 = parseTd("rOOt");
		final Parse cell3 = parseTd("non existing message");

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processContains(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("a message (expected)a message (actual)")));
		assertThat(cell2.text(), is(equalTo("rOOt (expected)a root message (actual)")));
		assertThat(cell3.text(), is(equalTo("non existing message")));

		assertThat(fixture.counts().right, is(2));
		assertThat(fixture.counts().wrong, is(1));
	}

	@Test
	public void testParseWithParameters() {
		final Fixture fixture = aFixture();
		final Parse cell1 = parseTd("no error");
		final Parse cell2 = parseTd("root");
		final Parse cell3 = parseTd("no error");
		final Parse cell4 = parseTd("no error");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("minlevel", "Info");
		parameters.put("thread", "thread2");
		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(fixture, cell1, list);
		analyzer.processContains(parameters);

		parameters.clear();
		parameters.put("minlevel", "error");
		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "main");
		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "thread5");
		analyzer = new LogEventAnalyzerImpl(fixture, cell4, list);
		analyzer.processContains(parameters);

		assertThat(cell1.text(), is(equalTo("no error (expected)no error (actual)")));
		assertThat(cell2.text(), is(equalTo("root")));
		assertThat(cell3.text(), is(equalTo("no error")));
		assertThat(cell4.text(), is(equalTo("no error")));

		assertThat(fixture.counts().right, is(3));
		assertThat(fixture.counts().wrong, is(1));
	}

	@Test
	public void testNotContains() {
		final Fixture fixture = aFixture();
		final Parse cell1 = parseTd("an error");
		final Parse cell2 = parseTd("toor");
		final Parse cell3 = parseTd("root");

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContains(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("an error")));
		assertThat(cell2.text(), is(equalTo("toor")));
		assertThat(cell3.text(), is(equalTo("root expecteda root message actual")));

		assertThat(fixture.counts().right, is(2));
		assertThat(fixture.counts().wrong, is(1));
	}

	public Fixture aFixture() {
		Counts counts = new Counts();
		final Fixture fixture = mock(Fixture.class);
		when(fixture.counts()).thenReturn(counts);
		return fixture;
	}

	@Test
	public void testContainsException() {
		final Fixture fixture = aFixture();
		final Parse cell1 = parseTd("xXx");
		final Parse cell2 = parseTd("RuntiMEException");
		final Parse cell3 = parseTd("IllegalStateException");

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(
				fixture, cell1, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processContainsException(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("xXx (expected)java.lang.RuntimeException: xxx (actual)")));
		assertThat(cell2.text(), is(equalTo("RuntiMEException (expected)java.lang.RuntimeException: xxx (actual)")));
		assertThat(cell3.text(), is(equalTo("IllegalStateException")));

		assertThat(fixture.counts().right, is(2));
		assertThat(fixture.counts().wrong, is(1));
	}

	@Test
	public void testNotContainsException() {
		final Fixture fixture = aFixture();
		final Parse cell1 = parseTd("Error message");
		final Parse cell2 = parseTd("IllegalStateException");
		final Parse cell3 = parseTd("Exception");

		LogEventAnalyzer analyzer = new LogEventAnalyzerImpl(fixture, cell1, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell2, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzerImpl(fixture, cell3, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("Error message")));
		assertThat(cell2.text(), is(equalTo("IllegalStateException")));
		assertThat(cell3.text(), is(equalTo("Exception expectedjava.lang.RuntimeException: xxx actual")));

		assertThat(fixture.counts().right, is(2));
		assertThat(fixture.counts().wrong, is(1));
	}
}
