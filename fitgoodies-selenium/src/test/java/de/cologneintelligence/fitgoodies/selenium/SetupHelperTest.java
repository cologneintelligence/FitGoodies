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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SetupHelperTest extends FitGoodiesTestCase {
	private SetupHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = DependencyManager.getOrCreate(SetupHelper.class);
	}

	@Test
	public void testDefaultValues() {
		assertThat(helper.getServerPort(), is(equalTo((Object) 4444)));
		assertThat(helper.getBrowserStartCommand(), is(equalTo("*firefox")));
		assertThat(helper.getBrowserURL(), is(equalTo("http://localhost")));
		assertThat(helper.getServerHost(), is(equalTo("localhost")));
		assertThat(helper.getSpeed(), is(nullValue()));
	}

	@Test
	public void testGettersAndSetters() {
		helper.setBrowserStartCommand("*chrome");
		assertThat(helper.getBrowserStartCommand(), is(equalTo("*chrome")));
		helper.setBrowserStartCommand("*opera");
		assertThat(helper.getBrowserStartCommand(), is(equalTo("*opera")));

		helper.setBrowserURL("http://example.org");
		assertThat(helper.getBrowserURL(), is(equalTo("http://example.org")));
		helper.setBrowserURL("http://example.com");
		assertThat(helper.getBrowserURL(), is(equalTo("http://example.com")));

		helper.setServerHost("127.0.0.1");
		assertThat(helper.getServerHost(), is(equalTo("127.0.0.1")));
		helper.setServerHost("192.168.0.1");
		assertThat(helper.getServerHost(), is(equalTo("192.168.0.1")));

		helper.setServerPort(1234);
		assertThat(helper.getServerPort(), is(equalTo((Object) 1234)));
		helper.setServerPort(4321);
		assertThat(helper.getServerPort(), is(equalTo((Object) 4321)));

		helper.setSpeed(123);
		assertThat(helper.getSpeed(), is(equalTo(123)));
		helper.setSpeed(321);
		assertThat(helper.getSpeed(), is(equalTo(321)));
		helper.setSpeed(null);
		assertThat(helper.getSpeed(), is(equalTo(null)));
	}
}
