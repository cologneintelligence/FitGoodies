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

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.Validator;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public final class LogEventAnalyzerTest extends FitGoodiesTestCase {
	private LoggingEvent[] list;

	@Mock
	private Validator validator;

	private Counts counts;

	@Before
	public void setUp() throws Exception {
		list = prepareCheckForGreenTest();
		counts = new Counts();
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
		Parse cell1 = parseTd("a message");
		Parse cell2 = parseTd("rOOt");
		Parse cell3 = parseTd("non existing message");

		when(validator.preProcess(cell1)).thenReturn("a message");
		when(validator.preProcess(cell2)).thenReturn("rOOt");
		when(validator.preProcess(cell3)).thenReturn("non existing message");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(
				counts, validator, cell1, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell2, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell3, list);
		analyzer.processContains(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("a message (expected)a message (actual)")));
		assertThat(cell2.text(), is(equalTo("rOOt (expected)a root message (actual)")));
		assertThat(cell3.text(), is(equalTo("non existing message")));

		assertThat(counts.right, is(2));
		assertThat(counts.wrong, is(1));
	}

	@Test
	public void testParseWithParameters() {
		Parse cell1 = parseTd("no error");
		Parse cell2 = parseTd("root");
		Parse cell3 = parseTd("no error 23");
		Parse cell4 = parseTd("42 no error");

		when(validator.preProcess(cell1)).thenReturn("no error");
		when(validator.preProcess(cell2)).thenReturn("root");
		when(validator.preProcess(cell3)).thenReturn("no error");
		when(validator.preProcess(cell4)).thenReturn("no error");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("minlevel", "Info");
		parameters.put("thread", "thread2");
		LogEventAnalyzer analyzer = new LogEventAnalyzer(counts, validator, cell1, list);
		analyzer.processContains(parameters);

		parameters.clear();
		parameters.put("minlevel", "error");
		analyzer = new LogEventAnalyzer(counts, validator, cell2, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "main");
		analyzer = new LogEventAnalyzer(counts, validator, cell3, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "thread5");
		analyzer = new LogEventAnalyzer(counts, validator, cell4, list);
		analyzer.processContains(parameters);

		assertThat(cell1.text(), is(equalTo("no error (expected)no error (actual)")));
		assertThat(cell2.text(), is(equalTo("root")));
		assertThat(cell3.text(), is(equalTo("no error")));
		assertThat(cell4.text(), is(equalTo("no error")));

		assertThat(counts.right, is(3));
		assertThat(counts.wrong, is(1));
	}

	@Test
	public void testNotContains() {
		Parse cell1 = parseTd("an error X");
		Parse cell2 = parseTd("toor X");
		Parse cell3 = parseTd("root Y");

		when(validator.preProcess(cell1)).thenReturn("an error");
		when(validator.preProcess(cell2)).thenReturn("toor");
		when(validator.preProcess(cell3)).thenReturn("root");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(counts, validator, cell1, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell2, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell3, list);
		analyzer.processNotContains(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("an error")));
		assertThat(cell2.text(), is(equalTo("toor")));
		assertThat(cell3.text(), is(equalTo("root expecteda root message actual")));

		assertThat(counts.right, is(2));
		assertThat(counts.wrong, is(1));
	}

	@Test
	public void testContainsException() {
		Parse cell1 = parseTd("xXx");
		Parse cell2 = parseTd("RuntiMEException");
		Parse cell3 = parseTd("IllegalStateException");

		when(validator.preProcess(cell1)).thenReturn("xXx");
		when(validator.preProcess(cell2)).thenReturn("RuntiMEException");
		when(validator.preProcess(cell3)).thenReturn("IllegalStateException");


		LogEventAnalyzer analyzer = new LogEventAnalyzer(
				counts, validator, cell1, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell2, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell3, list);
		analyzer.processContainsException(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("xXx (expected)java.lang.RuntimeException: xxx (actual)")));
		assertThat(cell2.text(), is(equalTo("RuntiMEException (expected)java.lang.RuntimeException: xxx (actual)")));
		assertThat(cell3.text(), is(equalTo("IllegalStateException")));

		assertThat(counts.right, is(2));
		assertThat(counts.wrong, is(1));
	}

	@Test
	public void testNotContainsException() {
		Parse cell1 = parseTd("Error message");
		Parse cell2 = parseTd("IllegalStateException");
		Parse cell3 = parseTd("Exception");

		when(validator.preProcess(cell1)).thenReturn("Error message");
		when(validator.preProcess(cell2)).thenReturn("IllegalStateException");
		when(validator.preProcess(cell3)).thenReturn("Exception");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(counts, validator, cell1, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell2, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(counts, validator, cell3, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		assertThat(cell1.text(), is(equalTo("Error message")));
		assertThat(cell2.text(), is(equalTo("IllegalStateException")));
		assertThat(cell3.text(), is(equalTo("Exception expectedjava.lang.RuntimeException: xxx actual")));

		assertThat(counts.right, is(2));
		assertThat(counts.wrong, is(1));
	}
}
