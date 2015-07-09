package de.cologneintelligence.fitgoodies.external;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
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
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>java</td></tr></table>");

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);

        verify(processWrapper).start("java");
    }

    @Test
    public void testStartFixtureStartsCommandWithoutArgs2() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td></tr></table>");

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);

        verify(processWrapper).start("ant");
    }

    @Test
    public void testStartFixtureStartsCommandWithOneArg() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td><td>package</td></tr></table>");

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);

        verify(processWrapper).start("ant", "package");
    }

    @Test
    public void testStartFixtureStartsCommandWithTwoArgs() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td><td>test</td><td>package</td></tr></table>");

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);

        verify(processWrapper).start("ant", "test", "package");
    }

    @Test
    public void testStartAndWaitFixtureStartsCommandWithoutArgs() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>java</td></tr></table>");

        when(processWrapper.startAndWait("java")).thenReturn(0);
        fixture.doTable(table);
        assertCounts(fixture.counts, table, 1, 0, 0, 0);
    }

    @Test
    public void testStartAndWaitFixtureStartsCommandWithoutArgs2() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>ant</td></tr></table>");

        when(processWrapper.startAndWait("ant")).thenReturn(1);

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 1, 0, 0);
    }

    @Test
    public void testStartAndWaitFixtureStartsCommandWithOneArg() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>ant</td><td>package</td></tr></table>");

        when(processWrapper.startAndWait("ant", "package")).thenReturn(0);
        fixture.doTable(table);
        assertCounts(fixture.counts, table, 1, 0, 0, 0);
    }

    @Test
    public void testChangeDir() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>changeDir</td><td>c:\\test</td></tr></table>");

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
        verify(processWrapper).changeDir("c:\\test");
    }

    @Test
    public void testReadDefaultSystemPropertiesFromSetupHelper() throws Exception {
        setupHelper.addProperty("bla");
        setupHelper.addProperty("blub");
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>ant</td><td>test-target</td></tr></table>");

        when(processWrapper.startAndWait("ant", "test-target", "bla", "blubb")).thenReturn(0);
        fixture.doTable(table);
        assertCounts(fixture.counts, table, 1, 0, 0, 0);
    }

}
