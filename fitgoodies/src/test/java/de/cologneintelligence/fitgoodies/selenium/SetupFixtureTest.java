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

package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumFactory;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SetupFixtureTest extends FitGoodiesFixtureTestCase<SetupFixture> {
	private SetupHelper helper;

    @Mock
    private CommandProcessor commandProcessor;

    @Override
    protected Class<SetupFixture> getFixtureClass() {
        return SetupFixture.class;
    }

    @Before
	public void setUp() throws Exception {
		helper = DependencyManager.getOrCreate(SetupHelper.class);

        SeleniumFactory factory = mock(SeleniumFactory.class);
		DependencyManager.inject(SeleniumFactory.class, factory);

		when(factory.createCommandProcessor("localhost", 4444, "*firefox", "http://localhost"))
				.thenReturn(commandProcessor);
	}

	@Test
	public void testHelperInteraction() throws Exception {
		useTable(
				tr("serverHost", "server-host"),
				tr("serverPort", "4444"),
				tr("browserStartCommand", "browser-Start-Command"),
				tr("browserURL", "browser-URL"),
				tr("speed", "400"),
				tr("timeout", "3000"),
				tr("retryTimeout", "$timeout"),
				tr("retryInterval", "10"),
				tr("takeScreenshots", "$screenshot"),
				tr("sleepBeforeScreenshot", "500"),
				tr("start", "start config"));


        preparePreprocessWithConversion(String.class, "server-host", "server-host");
        preparePreprocessWithConversion(String.class, "4444", "4444");
        preparePreprocessWithConversion(String.class, "browser-Start-Command", "browser-Start-Command");
        preparePreprocessWithConversion(String.class, "browser-URL", "browser-URL");
        preparePreprocessWithConversion(String.class, "400", "400");
        preparePreprocessWithConversion(String.class, "3000", "3000");
        preparePreprocessWithConversion(String.class, "$timeout", "40");
        preparePreprocessWithConversion(String.class, "10", "10");
        preparePreprocessWithConversion(String.class, "$screenshot", "true");
        preparePreprocessWithConversion(String.class, "500", "500");
        preparePreprocessWithConversion(String.class, "start config", "start config");

		helper.setCommandProcessor(commandProcessor);

		run();

        assertCounts(0, 0, 0, 0);
		assertThat(helper.getServerHost(), is(equalTo("server-host")));
		assertThat(helper.getServerPort(), is(equalTo((Object) 4444)));
		assertThat(helper.getBrowserStartCommand(), is(equalTo("browser-Start-Command")));
		assertThat(helper.getBrowserURL(), is(equalTo("browser-URL")));
		assertThat(helper.getSpeed(), is(equalTo(400)));
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
		useTable(tr("stop", ""));

		helper.setCommandProcessor(commandProcessor);

		final SetupFixture fixture = new SetupFixture();
		run();

        assertCounts(0, 0, 0, 0);

		verify(commandProcessor).stop();
	}

}
