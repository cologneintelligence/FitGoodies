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

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.Validator;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
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
        useTable(tr("a message", "rOOt", "non existing message"));
		FitCell cell1 = cell(0);
		FitCell cell2 = cell(1);
		FitCell cell3 = cell(2);

		when(validator.preProcess(cell1)).thenReturn("a message");
		when(validator.preProcess(cell2)).thenReturn("rOOt");
		when(validator.preProcess(cell3)).thenReturn("non existing message");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(
            validator, cell1, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell2, list);
		analyzer.processContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell3, list);
		analyzer.processContains(new HashMap<String, String>());

        lastFitTable.finishExecution();

		assertThat(cell1.getFitValue(), containsAll("a message", "expected",
            "a message", "actual"));
		assertThat(cell2.getFitValue(), containsAll("rOOt", "expected",
            "a root message", "actual"));
		assertThat(cell3.getFitValue(), is(equalTo("non existing message")));

		assertThat(lastFitTable.getCounts().right, is(2));
		assertThat(lastFitTable.getCounts().wrong, is(1));
	}

	@Test
	public void testParseWithParameters() {
        useTable(tr("no error", "root", "no error 23", "42 no error"));

        FitCell cell1 = cell(0);
        FitCell cell2 = cell(1);
        FitCell cell3 = cell(2);
        FitCell cell4 = cell(3);

		when(validator.preProcess(cell1)).thenReturn("no error");
		when(validator.preProcess(cell2)).thenReturn("root");
		when(validator.preProcess(cell3)).thenReturn("no error");
		when(validator.preProcess(cell4)).thenReturn("no error");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("minlevel", "Info");
		parameters.put("thread", "thread2");
		LogEventAnalyzer analyzer = new LogEventAnalyzer(validator, cell1, list);
		analyzer.processContains(parameters);

		parameters.clear();
		parameters.put("minlevel", "error");
		analyzer = new LogEventAnalyzer(validator, cell2, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "main");
		analyzer = new LogEventAnalyzer(validator, cell3, list);
		analyzer.processNotContains(parameters);

		parameters.clear();
		parameters.put("thread", "thread5");
		analyzer = new LogEventAnalyzer(validator, cell4, list);
		analyzer.processContains(parameters);

        lastFitTable.finishExecution();

		assertThat(cell1.getFitValue(), containsAll("no error", "expected",
            "no error" ,"actual"));
		assertThat(cell2.getFitValue(), is(equalTo("root")));
		assertThat(cell3.getFitValue(), is(equalTo("no error")));
		assertThat(cell4.getFitValue(), is(equalTo("no error")));

		assertThat(lastFitTable.getCounts().right, is(3));
		assertThat(lastFitTable.getCounts().wrong, is(1));
	}

	@Test
	public void testNotContains() {
        useTable(tr("a error X", "toor X", "root Y"));
		FitCell cell1 = cell(0);
		FitCell cell2 = cell(1);
		FitCell cell3 = cell(2);

		when(validator.preProcess(cell1)).thenReturn("an error");
		when(validator.preProcess(cell2)).thenReturn("toor");
		when(validator.preProcess(cell3)).thenReturn("root");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(validator, cell1, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell2, list);
		analyzer.processNotContains(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell3, list);
		analyzer.processNotContains(new HashMap<String, String>());

        lastFitTable.finishExecution();

        assertThat(cell1.getFitValue(), is(equalTo("an error")));
		assertThat(cell2.getFitValue(), is(equalTo("toor")));
		assertThat(cell3.getFitValue(), containsAll("root", "expected",
            "a root message", "actual"));

		assertThat(lastFitTable.getCounts().right, is(2));
		assertThat(lastFitTable.getCounts().wrong, is(1));
	}

	@Test
	public void testContainsException() {
		useTable(tr("xXx", "RuntiMEException", "IllegalStateException"));

        FitCell cell1 = cell(0);
        FitCell cell2 = cell(1);
        FitCell cell3 = cell(2);

        when(validator.preProcess(cell1)).thenReturn("xXx");
		when(validator.preProcess(cell2)).thenReturn("RuntiMEException");
		when(validator.preProcess(cell3)).thenReturn("IllegalStateException");


		LogEventAnalyzer analyzer = new LogEventAnalyzer(
            validator, cell1, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell2, list);
		analyzer.processContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell3, list);
		analyzer.processContainsException(new HashMap<String, String>());

        lastFitTable.finishExecution();

		assertThat(cell1.getFitValue(), containsAll("xXx", "expected",
            "java.lang.RuntimeException: xxx", "actual"));
		assertThat(cell2.getFitValue(), containsAll("RuntiMEException", "expected",
            "java.lang.RuntimeException: xxx", "actual"));
		assertThat(cell3.getFitValue(), is(equalTo("IllegalStateException")));

		assertThat(lastFitTable.getCounts().right, is(2));
		assertThat(lastFitTable.getCounts().wrong, is(1));
	}

	@Test
	public void testNotContainsException() {
        useTable(tr("Error message", "IllegalStateException", "Exception"));

        FitCell cell1 = cell(0);
        FitCell cell2 = cell(1);
        FitCell cell3 = cell(2);

		when(validator.preProcess(cell1)).thenReturn("Error message");
		when(validator.preProcess(cell2)).thenReturn("IllegalStateException");
		when(validator.preProcess(cell3)).thenReturn("Exception");

		LogEventAnalyzer analyzer = new LogEventAnalyzer(validator, cell1, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell2, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

		analyzer = new LogEventAnalyzer(validator, cell3, list);
		analyzer.processNotContainsException(new HashMap<String, String>());

        lastFitTable.finishExecution();

		assertThat(cell1.getFitValue(), is(equalTo("Error message")));
		assertThat(cell2.getFitValue(), is(equalTo("IllegalStateException")));
		assertThat(cell3.getFitValue(), containsAll(
            "Exception", "expected",
            "java.lang.RuntimeException: xxx", "actual"));

		assertThat(lastFitTable.getCounts().right, is(2));
		assertThat(lastFitTable.getCounts().wrong, is(1));
	}

    protected FitCell cell(int index) {
        return lastFitTable.rows().get(0).cells().get(index);
    }
}
