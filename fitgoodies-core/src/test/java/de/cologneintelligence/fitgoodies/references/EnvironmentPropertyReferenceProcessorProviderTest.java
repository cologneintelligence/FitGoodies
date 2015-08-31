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

package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnvironmentPropertyReferenceProcessorProviderTest extends FitGoodiesTestCase {

	public static final String ENV_VAR1 = "P_TEST_1";
	public static final String ENV_VAR2 = "P_TEST_2";

	private CellProcessorProvider provider;

	@Before
	public void setUp() {
		provider = new EnvironmentPropertyProcessorProvider();

		System.setProperty(ENV_VAR1, "1");
		System.setProperty(ENV_VAR2, "2");
	}

	@After
	public void clearDown() {
		System.clearProperty("P_TEST_1");
		System.clearProperty("P_TEST_2");
	}

	@Test
	public void testMatch() {
		assertThat(provider.canProcess("${getProperty(test)}"), is(true));
		assertThat(provider.canProcess("y${getProperty(other)}x"), is(true));
		assertThat(provider.canProcess("y${system.GETProperty(other)}x"), is(true));

		assertThat(provider.canProcess("y${system.Property(other)}x"), is(false));
		assertThat(provider.canProcess("y${getProperty()}x"), is(false));
	}

	@Test
	public void propertyIsReplaced() {
		assertThat(provider.create(">${getProperty(" + ENV_VAR1 + ")}<").preprocess(),
				is(equalTo(">1<")));
		assertThat(provider.create("${system.getProperty(" + ENV_VAR2 + ")}").preprocess(),
				is(equalTo("2")));
		assertThat(provider.create(">${getProperty(UNKNOWN_VALUE)}<").preprocess(),
				is(equalTo("><")));
	}
}
