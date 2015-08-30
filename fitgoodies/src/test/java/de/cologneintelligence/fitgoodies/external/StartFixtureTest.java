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

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class StartFixtureTest extends FitGoodiesFixtureTestCase<StartFixture> {
    @Mock
	private ProcessWrapper processWrapper;

	private SetupHelper setupHelper;

    @Override
    protected Class<StartFixture> getFixtureClass() {
        return StartFixture.class;
    }

    @Override
    protected StartFixture newInstance() throws InstantiationException, IllegalAccessException {
		return new StartFixture(processWrapper);
    }

    @Before
	public void prepareMocks() {
		setupHelper = new SetupHelper();
		DependencyManager.inject(SetupHelper.class, setupHelper);
	}

	@Test
	public void testStartFixtureStartsCommandWithoutArgs() throws Exception {
		useTable(tr("execute", "java"));

        preparePreprocessWithConversion(String.class, "java", "java");

		run();
		assertCounts(0, 0, 0, 0);

		verify(processWrapper).start("java");
	}

	@Test
	public void testStartFixtureStartsCommandWithoutArgs2() throws Exception {
		useTable(tr("execute", "prog"));

        preparePreprocessWithConversion(String.class, "prog", "ant");

		run();
		assertCounts(0, 0, 0, 0);

		verify(processWrapper).start("ant");
	}

	@Test
	public void testStartFixtureStartsCommandWithOneArg() throws Exception {
		useTable(tr("execute", "prog", "param"));

        preparePreprocessWithConversion(String.class, "prog", "ant");
        preparePreprocess("param", "package");

		run();
		assertCounts(0, 0, 0, 0);

		verify(processWrapper).start("ant", "package");
	}

	@Test
	public void testStartFixtureStartsCommandWithTwoArgs() throws Exception {
		useTable(tr("execute", "ant", "test", "package"));

        preparePreprocessWithConversion(String.class, "ant", "ant");
        preparePreprocess("test", "test");
        preparePreprocess("package", "package");


        run();
		assertCounts(0, 0, 0, 0);

		verify(processWrapper).start("ant", "test", "package");
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithoutArgs() throws Exception {
        String process = "java";

		useTable(tr("executeAndWait", "java2"));
        when(processWrapper.startAndWait(process)).thenReturn(0);

        preparePreprocessWithConversion(String.class, "java2", process);

		run();

        verify(processWrapper).startAndWait(process);
		assertCounts(1, 0, 0, 0);
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithoutArgs2() throws Exception {
		useTable(tr("executeAndWait", "ant"));
        preparePreprocessWithConversion(String.class, "ant", "ant");

        when(processWrapper.startAndWait("ant")).thenReturn(1);

		run();

		assertCounts(0, 1, 0, 0);
	}

	@Test
	public void testStartAndWaitFixtureStartsCommandWithOneArg() throws Exception {
		useTable(tr("executeAndWait", "ant", "package"));
		when(processWrapper.startAndWait("ant", "package")).thenReturn(0);

        preparePreprocessWithConversion(String.class, "ant", "ant");
        preparePreprocess("package", "package");

        run();

		assertCounts(1, 0, 0, 0);
	}

	@Test
	public void testChangeDir() throws Exception {
		useTable(tr("changeDir", "dir"));

        preparePreprocessWithConversion(String.class, "dir", "c:\\test");

		run();

		assertCounts(0, 0, 0, 0);
		verify(processWrapper).changeDir("c:\\test");
	}

	@Test
	public void testReadDefaultSystemPropertiesFromSetupHelper() throws Exception {
		useTable(tr("executeAndWait", "ant", "target"));
        preparePreprocessWithConversion(String.class, "ant", "ant");
        preparePreprocess("target", "test-target");

		when(processWrapper.startAndWait("ant", "test-target", "bla", "blubb")).thenReturn(0);
		setupHelper.addProperty("bla");
		setupHelper.addProperty("blub");

		run();

		assertCounts(1, 0, 0, 0);
	}

}
