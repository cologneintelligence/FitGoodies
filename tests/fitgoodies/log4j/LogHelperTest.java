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

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.jmock.Expectations;

import fitgoodies.FitGoodiesTestCase;

/**
 * @author jwierum
 * @version $Id$
 */
public final class LogHelperTest extends FitGoodiesTestCase {
	private static class DummyLogger implements AppenderAttachable {
		private Appender app1;
		private Appender app2;
		private final Appender baseAppender;

		public DummyLogger(final Appender base) {
			this.baseAppender = base;
		}

		@Override public void addAppender(final Appender appender) {
			if (app1 == null) {
				app1 = appender;
			} else {
				app2 = appender;
			}
		}

		@Override
		public Appender getAppender(final String name) {
			if (CaptureAppender.getAppenderNameFor("R").equals(name)) {
				return app1;
			} else if (CaptureAppender.getAppenderNameFor("stderr").equals(name)) {
				return app2;
			} else {
				return baseAppender;
			}
		}

		public Appender getExpected1() { return app1; }
		public Appender getExpected2() { return app2; }

		@Override public Enumeration<?> getAllAppenders() { return null; }
		@Override public boolean isAttached(final Appender arg0) { return true; }
		@Override public void removeAllAppenders() { }
		@Override public void removeAppender(final String arg0) { }
		@Override public void removeAppender(final Appender arg0) {

		}
	};

	public void testSingleton() {
		LogHelper helper = LogHelper.instance();
		assertNotNull(helper);
		assertSame(helper, LogHelper.instance());

		LogHelper.reset();
		assertNotSame(helper, LogHelper.instance());
	}

	public void testAppender() {
		final Appender baseAppender = mock(Appender.class);
		DummyLogger logger = new DummyLogger(baseAppender);

		checking(new Expectations() {{
			exactly(2).of(baseAppender).getName();
				will(returnValue("BaseAppender"));
		}});

		LogHelper.instance().addCaptureToLogger(logger, "R");
		LogHelper.instance().addCaptureToLogger(logger, "stderr");
		assertSame(logger.getExpected1(),
				LogHelper.instance().getCaptureAppender(logger, "R"));
		assertSame(logger.getExpected2(),
				LogHelper.instance().getCaptureAppender(logger, "stderr"));
	}

	public void testRegister() {
		final AppenderAttachable logger = mock(AppenderAttachable.class);
		final Appender appender = mock(Appender.class);

		checking(new Expectations() {{
			oneOf(appender).getName();
				will(returnValue("BaseAppender3"));
			oneOf(logger).getAppender("stdout"); will(returnValue(appender));
			oneOf(logger).addAppender(with(aNonNull(CaptureAppender.class)));
		}});

		LogHelper.instance().addCaptureToLogger(logger, "stdout");
	}

	public void testUnregister() {
		final AppenderAttachable logger = mock(AppenderAttachable.class);

		checking(new Expectations() {{
			oneOf(logger).removeAppender(CaptureAppender.getAppenderNameFor("R"));
		}});

		LogHelper.instance().remove(logger, "R");
	}

	public void testClear() {
		final AppenderAttachable logger = mock(AppenderAttachable.class);
		final Appender appender = mock(Appender.class);

		checking(new Expectations() {{
			oneOf(appender).getName(); will(returnValue("R"));
		}});

		final CaptureAppender dummyAppender = CaptureAppender.newAppenderFrom(appender);

		checking(new Expectations() {{
			oneOf(logger).getAppender(CaptureAppender.getAppenderNameFor("R"));
				will(returnValue(dummyAppender));
		}});

		LogHelper.instance().clear(logger, "R");
	}
}
