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

import com.thoughtworks.selenium.CommandProcessor;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SetupFixtureTest extends FitGoodiesTestCase {
    private SetupHelper helper;
    private SeleniumFactory factory;
    private CommandProcessor commandProcessor;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(SetupHelper.class);
        factory = mock(SeleniumFactory.class);
        commandProcessor = mock(CommandProcessor.class);
        DependencyManager.inject(SeleniumFactory.class, factory);

        when(factory.createCommandProcessor("localhost", 4444, "*firefox", "http://localhost"))
            .thenReturn(commandProcessor);
    }

    @Test
    public void testHelperInteraction() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>serverHost</td><td>server-host</td></tr>"
                + "<tr><td>serverPort</td><td>4444</td></tr>"
                + "<tr><td>browserStartCommand</td><td>browser-Start-Command</td></tr>"
                + "<tr><td>browserURL</td><td>browser-URL</td></tr>"
                + "<tr><td>speed</td><td>400</td></tr>"
                + "<tr><td>timeout</td><td>3000</td></tr>"
                + "<tr><td>retryTimeout</td><td>40</td></tr>"
                + "<tr><td>retryInterval</td><td>10</td></tr>"
                + "<tr><td>takeScreenshots</td><td>true</td></tr>"
                + "<tr><td>sleepBeforeScreenshot</td><td>500</td></tr>"
                + "<tr><td>start</td><td>start config</td></tr>"
                + "</table>"
                );

        helper.setCommandProcessor(commandProcessor);

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 0)));
        assertThat(helper.getServerHost(), is(equalTo("server-host")));
        assertThat(helper.getServerPort(), is(equalTo((Object) 4444)));
        assertThat(helper.getBrowserStartCommand(), is(equalTo("browser-Start-Command")));
        assertThat(helper.getBrowserURL(), is(equalTo("browser-URL")));
        assertThat(helper.getSpeed(), is(equalTo(Integer.valueOf(400))));
        assertThat(helper.getTimeout(), is(equalTo((Object) 3000L)));
        assertThat(helper.getRetryTimeout(), is(equalTo((Object) 40L)));
        assertThat(helper.getRetryInterval(), is(equalTo((Object) 10L)));
        assertThat(helper.getTakeScreenshots(), is(true));
        assertThat(helper.sleepBeforeScreenshot(), is(equalTo((Object) 500L)));
        assertThat(helper.getCommandProcessor(), not(CoreMatchers.is(nullValue())));

        verify(commandProcessor).start("start config");
        verify(commandProcessor).doCommand("setTimeout", new String[]{"3000"});
    }

    @Test
    public void testHelperInteractionStopProcessor() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>stop</td><td></td></tr>"
                + "</table>"
                );

        helper.setCommandProcessor(commandProcessor );

        final SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 0)));

        verify(commandProcessor).stop();
    }

}
