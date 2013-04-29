package de.cologneintelligence.fitgoodies.external;

import org.jmock.Expectations;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import fit.Parse;

public class StartFixtureTest extends FitGoodiesTestCase {
    private ProcessWrapper processWrapper;
    private StartFixture fixture;

    @Override
    public void setUp() {
        processWrapper = mock(ProcessWrapper.class);
        fixture = new StartFixture(processWrapper);
    }

    public void testStartFixtureStartsCommandWithoutArgs() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>java</td></tr></table>");

        checking(new Expectations(){{
            oneOf(processWrapper).start("java");
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
    }

    public void testStartFixtureStartsCommandWithoutArgs2() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td></tr></table>");

        checking(new Expectations() {{
            oneOf(processWrapper).start("ant");
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
    }

    public void testStartFixtureStartsCommandWithOneArg() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td><td>package</td></tr></table>");

        checking(new Expectations() {{
            oneOf(processWrapper).start("ant", "package");
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
    }

    public void testStartFixtureStartsCommandWithTwoArgs() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>execute</td><td>ant</td><td>test</td><td>package</td></tr></table>");

        checking(new Expectations() {{
            oneOf(processWrapper).start("ant", "test", "package");
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
    }

    public void testStartAndWaitFixtureStartsCommandWithoutArgs() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>java</td></tr></table>");

        checking(new Expectations(){{
            oneOf(processWrapper).startAndWait("java");
            will(returnValue(0));
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 1, 0, 0, 0);
    }

    public void testStartAndWaitFixtureStartsCommandWithoutArgs2() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>ant</td></tr></table>");

        checking(new Expectations() {{
            oneOf(processWrapper).startAndWait("ant");
            will(returnValue(1));
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 1, 0, 0);
    }

    public void testStartAndWaitFixtureStartsCommandWithOneArg() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>executeAndWait</td><td>ant</td><td>package</td></tr></table>");

        checking(new Expectations() {{
            oneOf(processWrapper).startAndWait("ant", "package");
            will(returnValue(0));
        }});

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 1, 0, 0, 0);
    }

    public void testChangeDir() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr><tr><td>changeDir</td><td>c:\\test</td></tr></table>");

        checking(new Expectations() {
            {
                oneOf(processWrapper).changeDir("c:\\test");
            }
        });

        fixture.doTable(table);
        assertCounts(fixture.counts, table, 0, 0, 0, 0);
    }
}
