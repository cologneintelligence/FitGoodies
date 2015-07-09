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
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    }

    private LogHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(LogHelper.class);
    }

    @Test
    public void testAppender() {
        final Appender baseAppender = mock(Appender.class);
        DummyLogger logger = new DummyLogger(baseAppender);

        when(baseAppender.getName()).thenReturn("BaseAppender");

        helper.addCaptureToLogger(logger, "R");
        helper.addCaptureToLogger(logger, "stderr");
        assertThat(helper.getCaptureAppender(logger, "R"), is(sameInstance(logger.getExpected1())));
        assertThat(helper.getCaptureAppender(logger, "stderr"), is(sameInstance(logger.getExpected2())));
    }

    @Test
    public void testRegister() {
        final AppenderAttachable logger = mock(AppenderAttachable.class);
        final Appender appender = mock(Appender.class);

        when(appender.getName()).thenReturn("BaseAppender3");
        when(logger.getAppender("stdout")).thenReturn(appender);

        helper.addCaptureToLogger(logger, "stdout");
        verify(logger).addAppender(argThat(any(CaptureAppender.class)));
    }

    @Test
    public void testUnregister() {
        final AppenderAttachable logger = mock(AppenderAttachable.class);

        helper.remove(logger, "R");
        verify(logger).removeAppender(CaptureAppender.getAppenderNameFor("R"));
    }

    @Test
    public void testClear() {
        final AppenderAttachable logger = mock(AppenderAttachable.class);
        final Appender appender = mock(Appender.class);

        when(appender.getName()).thenReturn("R");

        final CaptureAppender dummyAppender = CaptureAppender.newAppenderFrom(appender);

        when(logger.getAppender(CaptureAppender.getAppenderNameFor("R"))).thenReturn(dummyAppender);
        helper.clear(logger, "R");
    }
}
