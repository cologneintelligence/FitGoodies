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
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class CaptureAppenderTest extends FitGoodiesTestCase {
	private static class TestFilter extends Filter {
		@Override
		public int decide(final LoggingEvent arg0) {
			return 0;
		}
	}

	private Appender appenderMock;
	private Filter filterMock;

	@Before
	public void setUp() throws Exception {
		appenderMock = mock(Appender.class);
		filterMock = new TestFilter();
	}

	@Test
	public void testStorage() {
		when(appenderMock.getName()).thenReturn("BaseAppender");

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		LoggingEvent ev1 = event("fqdn", 42, Level.DEBUG, "message",
				"thread", throwableInfo("x"), "ndc");

		LoggingEvent ev2 = event("fqdn2", 42, Level.ERROR, "warning",
				"thread2", throwableInfo("y"), "ndc2");

		appender.doAppend(ev1);
		appender.doAppend(ev2);

		LoggingEvent[] events = appender.getAllEvents();
		assertThat(events[0], is(sameInstance(ev1)));
		assertThat(events[1], is(sameInstance(ev2)));
	}

	@Test
	public void testReset() {
		when(appenderMock.getName()).thenReturn("BaseAppender");

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		LoggingEvent ev1 = event("fqdn", 42, Level.DEBUG, "message",
				"thread", throwableInfo("x"), "ndc");

		LoggingEvent ev2 = event("fqdn2", 42, Level.ERROR, "warning",
				"thread2", throwableInfo("y"), "ndc2");

		appender.doAppend(ev1);
		appender.doAppend(ev2);

		appender.clear();
		LoggingEvent[] events = appender.getAllEvents();
		assertThat(events.length, is(equalTo((Object) 0)));
	}

	@Test
	public void testParentValues() {
		when(appenderMock.getName()).thenReturn("BaseAppender");
		when(appenderMock.getFilter()).thenReturn(filterMock);

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		assertThat(appender.getFilter(), is(sameInstance(filterMock)));
		assertThat(appender.getName(), is(equalTo("BaseAppender-fitgoodiescapture")));
	}

	@Test
	public void testDefaultValues() {
		when(appenderMock.getName()).thenReturn("BaseAppender");

		CaptureAppender appender = CaptureAppender.newAppenderFrom(appenderMock);

		appender.clearFilters();
		appender.addFilter(new Filter() {
			@Override
			public int decide(final LoggingEvent event) {
				return Filter.ACCEPT;
			}
		});

		appender.close();
		appender.setName("x");
		appender.setLayout(new Layout() {
			@Override
			public String format(final LoggingEvent event) {
				return null;
			}

			@Override
			public boolean ignoresThrowable() {
				return false;
			}

			@Override
			public void activateOptions() {
			}
		});

		assertThat(appender.getName(), is(equalTo(CaptureAppender.getAppenderNameFor("BaseAppender"))));
		assertThat(appender.requiresLayout(), is(false));

	}

	@Test
	public void testAppenderName() {
		assertThat(CaptureAppender.getAppenderNameFor("test"), is(equalTo("test-fitgoodiescapture")));
		assertThat(CaptureAppender.getAppenderNameFor("test2"), is(equalTo("test2-fitgoodiescapture")));
	}

	private LoggingEvent event(String fqnOfCategoryClass, long timeStamp, Level level, Object message,
	                           String threadName, ThrowableInformation throwable, String ndc) {
		return new LoggingEvent(fqnOfCategoryClass, null, timeStamp, level, message,
				threadName, throwable, ndc, null, Collections.emptyMap());
	}

	protected ThrowableInformation throwableInfo(String text) {
		return new ThrowableInformation(new RuntimeException(text));
	}
}
