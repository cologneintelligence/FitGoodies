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
import de.cologneintelligence.fitgoodies.Parse;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public final class SetupFixtureTest extends FitGoodiesTestCase {
	@Test
	public void testParse() throws Exception {
		final LoggerProvider provider = mock(LoggerProvider.class);
		final AppenderAttachable attachable1 =
			mock(AppenderAttachable.class, "attachable1");
		final AppenderAttachable attachable2 =
			mock(AppenderAttachable.class, "attachable2");
		final AppenderAttachable attachable3 =
			mock(AppenderAttachable.class, "attachable3");
		final Appender appender1 = mock(Appender.class, "appender1");
		final Appender appender2 = mock(Appender.class, "appender2");
		final Appender appender3 = mock(Appender.class, "appender3");

		SetupFixture fixture = new SetupFixture(provider);

		Parse table = parseTable(
				tr("monitor", "com.example.class1", "R"),
				tr("monitor", "com.example.testclass2", "stdout"),
				tr("monitorRoot", "R</td></tr></table>"));

		when(provider.getLogger("com.example.class1")).thenReturn(attachable1);
		when(attachable1.getAppender("R")).thenReturn(appender1);
		when(appender1.getName()).thenReturn("BaseAppender3");

		when(provider.getLogger("com.example.testclass2")).thenReturn(attachable2);
		when(attachable2.getAppender("stdout")).thenReturn(appender2);
		when(appender2.getName()).thenReturn("BaseAppender3");

		when(provider.getRootLogger()).thenReturn(attachable3);
		when(attachable3.getAppender("R")).thenReturn(appender3);
		when(appender3.getName()).thenReturn("BaseAppender3");

		fixture.doTable(table);

		verify(attachable1).addAppender(argThat(any(CaptureAppender.class)));
		verify(attachable2).addAppender(argThat(any(CaptureAppender.class)));
		verify(attachable3).addAppender(argThat(any(CaptureAppender.class)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
	}

	@Test
	public void testParse2() throws Exception {
		final LoggerProvider provider = mock(LoggerProvider.class);
		final AppenderAttachable attachable1 =
			mock(AppenderAttachable.class, "attachable1");
		final AppenderAttachable attachable2 =
			mock(AppenderAttachable.class, "attachable2");
		final Appender appender1 = mock(Appender.class, "appender1");
		final Appender appender2 = mock(Appender.class, "appender2");

		when(appender1.getName()).thenReturn("BaseAppender1");
		when(appender2.getName()).thenReturn("BaseAppender2");

		when(provider.getLogger("com.example.class2")).thenReturn(attachable1);
		when(attachable1.getAppender("stderr")).thenReturn(appender1);

		when(provider.getLogger("com.example.testclass1")).thenReturn(attachable2);
		when(attachable2.getAppender("R")).thenReturn(appender2);

		SetupFixture fixture = new SetupFixture(provider);

		Parse table = parseTable(
				tr("monitor", "com.example.class2", "stderr"),
				tr("monitor", "com.example.testclass1", "R"));

		fixture.doTable(table);
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

		verify(attachable1).addAppender(argThat(any(CaptureAppender.class)));
		verify(attachable2).addAppender(argThat(any(CaptureAppender.class)));
	}

	@Test
	public void testClear() {
		final LoggerProvider provider = mock(LoggerProvider.class, "provider");
		final AppenderAttachable logger = mock(AppenderAttachable.class, "logger");
		final CaptureAppender appender = mock(CaptureAppender.class, "appender");

		when(provider.getLogger("com.example.class2")).thenReturn(logger);
		when(logger.getAppender(CaptureAppender.getAppenderNameFor("stderr")))
				.thenReturn(appender);

		SetupFixture fixture = new SetupFixture(provider);

		Parse table = parseTable(tr("clear", "com.example.class2", "stderr"));

		fixture.doTable(table);

		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));

		verify(appender).clear();
	}
}
