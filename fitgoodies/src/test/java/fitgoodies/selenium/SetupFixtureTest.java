/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.selenium;

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: SetupFixtureTest.java 4 2009-08-27 16:10:18Z jochen_wierum $
 * @author kmussawisade
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
	public final void testHelperInteraction() throws Exception {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>serverHost</td><td>server-host</td></tr>"
				+ "<tr><td>serverPort</td><td>4444</td></tr>"
				+ "<tr><td>browserStartCommand</td><td>browser-Start-Command</td></tr>"
				+ "<tr><td>browserURL</td><td>browser-URL</td></tr>"
				+ "<tr><td>start</td><td>start config</td></tr>"
				+ "</table>"
				);

		final CommandProcessor commandProcessor = mock(CommandProcessor.class);
		
		checking(new Expectations(){{
			oneOf(commandProcessor).start("start config");
		}});
		
		SetupHelper.instance().setCommandProcessor(commandProcessor );

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);
		
		assertEquals(0, fixture.counts.exceptions);
		assertEquals("server-host", SetupHelper.instance().getServerHost());
		assertEquals(4444, SetupHelper.instance().getServerPort());
		assertEquals("browser-Start-Command", SetupHelper.instance().getBrowserStartCommand());
		assertEquals("browser-URL", SetupHelper.instance().getBrowserURL());		
		assertNotNull(SetupHelper.instance().getCommandProcessor());
	}

	public final void testHelperInteractionStopProcessor() throws Exception {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>stop</td><td></td></tr>"
				+ "</table>"
				);

		final CommandProcessor commandProcessor = mock(CommandProcessor.class);
		
		checking(new Expectations(){{
			oneOf(commandProcessor).stop();
		}});
		
		SetupHelper.instance().setCommandProcessor(commandProcessor );

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);
		
		assertEquals(0, fixture.counts.exceptions);
		assertNotSame(commandProcessor, SetupHelper.instance().getCommandProcessor());
	}

}
