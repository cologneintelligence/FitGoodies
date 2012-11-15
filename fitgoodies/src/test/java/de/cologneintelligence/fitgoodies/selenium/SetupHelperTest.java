/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.selenium;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class SetupHelperTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.getOrCreate(SetupHelper.class);
    }

    public void testDefaultValues() {
        assertEquals(4444, helper.getServerPort());
        assertEquals("*firefox", helper.getBrowserStartCommand());
        assertEquals("http://localhost", helper.getBrowserURL());
        assertEquals("localhost", helper.getServerHost());
        assertNull(helper.getSpeed());
    }

    public void testGettersAndSetters() {
        helper.setBrowserStartCommand("*chrome");
        assertEquals("*chrome", helper.getBrowserStartCommand());
        helper.setBrowserStartCommand("*opera");
        assertEquals("*opera", helper.getBrowserStartCommand());

        helper.setBrowserURL("http://example.org");
        assertEquals("http://example.org", helper.getBrowserURL());
        helper.setBrowserURL("http://example.com");
        assertEquals("http://example.com", helper.getBrowserURL());

        helper.setServerHost("127.0.0.1");
        assertEquals("127.0.0.1", helper.getServerHost());
        helper.setServerHost("192.168.0.1");
        assertEquals("192.168.0.1", helper.getServerHost());

        helper.setServerPort(1234);
        assertEquals(1234, helper.getServerPort());
        helper.setServerPort(4321);
        assertEquals(4321, helper.getServerPort());

        helper.setSpeed(123);
        assertEquals(Integer.valueOf(123), helper.getSpeed());
        helper.setSpeed(321);
        assertEquals(Integer.valueOf(321), helper.getSpeed());
        helper.setSpeed(null);
        assertEquals(null, helper.getSpeed());
    }
}
