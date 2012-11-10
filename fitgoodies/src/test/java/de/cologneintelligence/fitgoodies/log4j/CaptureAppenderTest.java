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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.jmock.Expectations;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.log4j.CaptureAppender;


/**
 * @author jwierum
 * @version $Id$
 */
public final class CaptureAppenderTest extends FitGoodiesTestCase {
	private static class TestFilter extends Filter {
		@Override public int decide(final LoggingEvent arg0) { return 0; }
	}

	private Appender appenderMock;
	private Filter filterMock;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		appenderMock = mock(Appender.class);
		filterMock = new TestFilter();
	}

	public void testStorage() {
		checking(new Expectations() {{
			oneOf(appenderMock).getName(); will(returnValue("BaseAppender"));
			oneOf(appenderMock).getFilter();
			oneOf(appenderMock).getFilter();
		}});

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		Logger logger = null;

		Map<String, String> props = new HashMap<String, String>();
		LoggingEvent ev1 = new LoggingEvent("fqdn", logger, 42, Level.DEBUG, "message",
				"thread", new ThrowableInformation(new RuntimeException("x")),
				"ndc", null, props);

		LoggingEvent ev2 = new LoggingEvent("fqdn2", logger, 42, Level.ERROR, "warning",
				"thread2", new ThrowableInformation(new RuntimeException("y")),
				"ndc2", null, props);

		appender.doAppend(ev1);
		appender.doAppend(ev2);

		LoggingEvent[] events = appender.getAllEvents();
		assertSame(ev1, events[0]);
		assertSame(ev2, events[1]);
	}

	public void testReset() {
		checking(new Expectations() {{
			oneOf(appenderMock).getName(); will(returnValue("BaseAppender"));
			oneOf(appenderMock).getFilter();
			oneOf(appenderMock).getFilter();
		}});

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		Logger logger = null;

		Map<String, String> props = new HashMap<String, String>();
		LoggingEvent ev1 = new LoggingEvent("fqdn", logger, 42, Level.DEBUG, "message",
				"thread", new ThrowableInformation(new RuntimeException("x")),
				"ndc", null, props);

		LoggingEvent ev2 = new LoggingEvent("fqdn2", logger, 42, Level.ERROR, "warning",
				"thread2", new ThrowableInformation(new RuntimeException("y")),
				"ndc2", null, props);

		appender.doAppend(ev1);
		appender.doAppend(ev2);

		appender.clear();
		LoggingEvent[] events = appender.getAllEvents();
		assertEquals(0, events.length);
	}

	public void testParentValues() {
		checking(new Expectations() {{
			oneOf(appenderMock).getName(); will(returnValue("BaseAppender"));
			oneOf(appenderMock).getFilter(); will(returnValue(filterMock));
		}});

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		assertSame(filterMock, appender.getFilter());
		assertEquals("BaseAppender-fitgoodiescapture", appender.getName());
	}

	public void testDefaultValues() {
		checking(new Expectations() {{
			oneOf(appenderMock).getName(); will(returnValue("BaseAppender"));
		}});

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		appender.clearFilters();
		appender.addFilter(new Filter() {
			@Override public int decide(final LoggingEvent event) { return Filter.ACCEPT; }
		});

		appender.close();
		appender.setName("x");
		appender.setLayout(new Layout() {
			@Override public String format(final LoggingEvent event) { return null; }
			@Override public boolean ignoresThrowable() { return false; }
			@Override public void activateOptions() { }
		});

		assertEquals(CaptureAppender.getAppenderNameFor("BaseAppender"), appender.getName());
		assertFalse(appender.requiresLayout());
	}

	public void testAppenderName() {
		assertEquals("test-fitgoodiescapture", CaptureAppender.getAppenderNameFor("test"));
		assertEquals("test2-fitgoodiescapture", CaptureAppender.getAppenderNameFor("test2"));
	}
}
