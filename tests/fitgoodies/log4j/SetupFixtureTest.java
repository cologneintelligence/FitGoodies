/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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

package fitgoodies.log4j;

import java.text.ParseException;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.jmock.Expectations;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * @author jwierum
 * @version $Id: SetupFixtureTest.java 199 2009-08-21 15:19:47Z jwierum $
 */
public final class SetupFixtureTest extends FitGoodiesTestCase {
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

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>monitor</td><td>com.example.class1</td><td>R</td></tr>"
				+ "<tr><td>monitor</td><td>com.example.testclass2</td><td>stdout</td></tr>"
				+ "<tr><td>monitorRoot</td><td>R</td></tr></table>");

		checking(new Expectations() {{
			oneOf(provider).getLogger("com.example.class1");
				will(returnValue(attachable1));
			oneOf(attachable1).getAppender("R");
				will(returnValue(appender1));
			oneOf(appender1).getName();
				will(returnValue("BaseAppender3"));
			oneOf(attachable1).addAppender(with(any(CaptureAppender.class)));

			oneOf(provider).getLogger("com.example.testclass2");
				will(returnValue(attachable2));
			oneOf(attachable2).getAppender("stdout");
				will(returnValue(appender2));
			oneOf(appender2).getName();
				will(returnValue("BaseAppender3"));
			oneOf(attachable2).addAppender(with(any(CaptureAppender.class)));

			oneOf(provider).getRootLogger();
				will(returnValue(attachable3));
			oneOf(attachable3).getAppender("R");
				will(returnValue(appender3));
			oneOf(appender3).getName();
				will(returnValue("BaseAppender3"));
			oneOf(attachable3).addAppender(with(any(CaptureAppender.class)));
		}});

		fixture.doTable(table);
		assertEquals(0, fixture.counts.exceptions);
	}

	public void testParse2() throws Exception {
		final LoggerProvider provider = mock(LoggerProvider.class);
		final AppenderAttachable attachable1 =
			mock(AppenderAttachable.class, "attachable1");
		final AppenderAttachable attachable2 =
			mock(AppenderAttachable.class, "attachable2");
		final Appender appender1 = mock(Appender.class, "appender1");
		final Appender appender2 = mock(Appender.class, "appender2");

		checking(new Expectations() {{
			oneOf(appender1).getName(); will(returnValue("BaseAppender1"));
			oneOf(appender2).getName(); will(returnValue("BaseAppender2"));

			oneOf(provider).getLogger("com.example.class2");
				will(returnValue(attachable1));
			oneOf(attachable1).getAppender("stderr");
				will(returnValue(appender1));
			oneOf(attachable1).addAppender(with(any(CaptureAppender.class)));

			oneOf(provider).getLogger("com.example.testclass1");
				will(returnValue(attachable2));
			oneOf(attachable2).getAppender("R");
				will(returnValue(appender2));
			oneOf(attachable2).addAppender(with(any(CaptureAppender.class)));
		}});

		SetupFixture fixture = new SetupFixture(provider);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>monitor</td><td>com.example.class2</td><td>stderr</td></tr>"
				+ "<tr><td>monitor</td><td>com.example.testclass1</td><td>R</td></tr>"
				+ "</table>");

		fixture.doTable(table);
		assertEquals(0, fixture.counts.exceptions);
	}

	public void testClear() throws ParseException {
		final LoggerProvider provider = mock(LoggerProvider.class, "provider");
		final AppenderAttachable logger =
			mock(AppenderAttachable.class, "logger");
		final Appender appender = mock(Appender.class, "appender");

		checking(new Expectations() {{
			oneOf(appender).getName(); will(returnValue("stderr"));
		}});

		final CaptureAppender dummyCaptureAppender =
			CaptureAppender.newAppenderFrom(appender);

		checking(new Expectations() {{
			oneOf(appender).getName();
				will(returnValue("stderr"));

			exactly(2).of(provider).getLogger("com.example.class2");
				will(returnValue(logger));
			oneOf(logger).getAppender("stderr");
				will(returnValue(appender));
			oneOf(logger).addAppender(with(any(CaptureAppender.class)));

			oneOf(logger).getAppender(CaptureAppender.getAppenderNameFor("stderr"));
				will(returnValue(dummyCaptureAppender));
		}});

		SetupFixture fixture = new SetupFixture(provider);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>monitor</td><td>com.example.class2</td><td>stderr</td></tr>"
				+ "<tr><td>clear</td><td>com.example.class2</td><td>stderr</td></tr>"
				+ "</table>");

		fixture.doTable(table);

		assertEquals(0, fixture.counts.exceptions);
	}
}
