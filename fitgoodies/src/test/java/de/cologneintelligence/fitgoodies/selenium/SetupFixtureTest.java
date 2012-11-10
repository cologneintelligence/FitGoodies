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

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

/**
 * @author kmussawisade
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.getOrCreate(SetupHelper.class);
    }

    public final void testHelperInteraction() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>serverHost</td><td>server-host</td></tr>"
                + "<tr><td>serverPort</td><td>4444</td></tr>"
                + "<tr><td>browserStartCommand</td><td>browser-Start-Command</td></tr>"
                + "<tr><td>browserURL</td><td>browser-URL</td></tr>"
                + "<tr><td>speed</td><td>400</td></tr>"
                + "<tr><td>timeout</td><td>40</td></tr>"
                + "<tr><td>interval</td><td>10</td></tr>"
                + "<tr><td>takeScreenshots</td><td>true</td></tr>"
                + "<tr><td>sleepBeforeScreenshot</td><td>500</td></tr>"
                + "<tr><td>start</td><td>start config</td></tr>"
                + "</table>"
                );

        final CommandProcessor commandProcessor = mock(CommandProcessor.class);

        checking(new Expectations(){{
            oneOf(commandProcessor).start("start config");
        }});

        helper.setCommandProcessor(commandProcessor );

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        assertEquals("server-host", helper.getServerHost());
        assertEquals(4444, helper.getServerPort());
        assertEquals("browser-Start-Command", helper.getBrowserStartCommand());
        assertEquals("browser-URL", helper.getBrowserURL());
        assertEquals("400", helper.getSpeed());
        assertEquals(40L, helper.getTimeout());
        assertEquals(10L, helper.getInterval());
        assertEquals(true, helper.getTakeScreenshots());
        assertEquals(500L, helper.sleepBeforeScreenshot());
        assertNotNull(helper.getCommandProcessor());
    }

    public final void testHelperInteractionStopProcessor() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>stop</td><td></td></tr>"
                + "</table>"
                );

        final CommandProcessor commandProcessor = mock(CommandProcessor.class);

        checking(new Expectations(){{
            oneOf(commandProcessor).stop();
        }});

        helper.setCommandProcessor(commandProcessor );

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
    }

}
