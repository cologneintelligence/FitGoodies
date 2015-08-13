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

package de.cologneintelligence.fitgoodies.external;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class StartFixtureTest extends FitGoodiesTestCase {
	private ProcessWrapper processWrapper;
	private StartFixture fixture;
	private SetupHelper setupHelper;

	@Before
	public void prepareMocks() {
		processWrapper = mock(ProcessWrapper.class);
		fixture = new StartFixture(processWrapper);
		setupHelper = new SetupHelper();
		DependencyManager.inject(SetupHelper.class, setupHelper);
	}

	@Test
	public void testStartFixtureStartsCommandWithoutArgs() throws Exception {
		final Parse table = parseTable(tr("execute", "java"));

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 0);

		verify(processWrapper).start("java");
	}

	@Test
	public void testStartFixtureStartsCommandWithoutArgs2() throws Exception {
		final Parse table = parseTable(tr("execute", "ant"));

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 0);

		verify(processWrapper).start("ant");
	}

	@Test
	public void testStartFixtureStartsCommandWithOneArg() throws Exception {
		final Parse table = parseTable(tr("execute", "ant", "package"));

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 0);

		verify(processWrapper).start("ant", "package");
	}

	@Test
	public void testStartFixtureStartsCommandWithTwoArgs() throws Exception {
		final Parse table = parseTable(tr("execute", "ant", "test", "package"));

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 0);

		verify(processWrapper).start("ant", "test", "package");
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithoutArgs() throws Exception {
		final Parse table = parseTable(tr("executeAndWait", "java"));

		when(processWrapper.startAndWait("java")).thenReturn(0);
		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 1, 0, 0, 0);
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithoutArgs2() throws Exception {
		final Parse table = parseTable(tr("executeAndWait", "ant"));

		when(processWrapper.startAndWait("ant")).thenReturn(1);

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 1, 0, 0);
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithOneArg() throws Exception {
		final Parse table = parseTable(tr("executeAndWait", "ant", "package"));

		when(processWrapper.startAndWait("ant", "package")).thenReturn(0);
		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 1, 0, 0, 0);
	}

	@Test
	public void testChangeDir() throws Exception {
		final Parse table = parseTable(tr("changeDir", "c:\\test"));

		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 0, 0, 0, 0);
		verify(processWrapper).changeDir("c:\\test");
	}

	@Test
	public void testReadDefaultSystemPropertiesFromSetupHelper() throws Exception {
		setupHelper.addProperty("bla");
		setupHelper.addProperty("blub");
		final Parse table = parseTable(tr("executeAndWait", "ant", "test-target"));

		when(processWrapper.startAndWait("ant", "test-target", "bla", "blubb")).thenReturn(0);
		fixture.doTable(table);
		assertCounts(fixture.counts(), table, 1, 0, 0, 0);
	}

}
