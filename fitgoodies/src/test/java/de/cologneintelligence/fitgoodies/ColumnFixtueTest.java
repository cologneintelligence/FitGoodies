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


package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ColumnFixtueTest extends FitGoodiesTestCase {
    @SuppressWarnings("unused")
    public static class NumberObjFixture extends ColumnFixture {
        public Integer testNr;
        public Integer testNrAtStart;

        public Integer number;
        public final Integer n() {
            return number;
        }

        public boolean upCalled;
        public boolean downCalled;

        @Override
        public void setUp() {
            upCalled = true;
            testNrAtStart = testNr;
        }
        @Override
        public void tearDown() { downCalled = true; }
    }

    public static class StringObjFixture extends ColumnFixture {
        public String string;
        public final String s() {
            if ("null".equals(string)) {
                return null;
            } else {
                return string;
            }
        }
    }

    private static class ErrorFixture extends ColumnFixture {
        private boolean downCalled = false;
        public boolean isDownCalled() { return downCalled; }
        @Override public void tearDown() { downCalled = true; }
    }

    private StringObjFixture stringObjFixture;
    private NumberObjFixture numberObjFixture;

    @Before
    public void setUp() throws Exception {
        stringObjFixture = new StringObjFixture();
        numberObjFixture = new NumberObjFixture();
    }

    @Test
    public void testSimpleStringCases() {
        Parse table = parseTable(
                tr("string", "s()"),
                tr("x", "x</td></tr></table>"));
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

        table = parseTable(
                tr("string", "s()"),
                tr("x", "y</td></tr></table>"));
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
    }

    @Test
    public void testSimpleNumberCase() {
        Parse table = parseTable(
                tr("number", "n()"),
                tr("2", "2"));
        numberObjFixture.doTable(table);
        assertThat(numberObjFixture.counts().right, is(equalTo((Object) 1)));
    }

    @Test
    public void testCrossReferencesWithoutException() {
        Parse table = parseTable(
                tr("string", "s()"),
                tr("matched", "${test}"));

        CrossReferenceHelper helper = DependencyManager.getOrCreate(
                CrossReferenceHelper.class);
        helper.getProcessors().add(new CrossReferenceProcessorMock("test"));

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

        table = parseTable(
                tr("string", "s()"),
                tr("test2", "${test}"));
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
    }

    @Test
    public void testCrossReferencesWithException() {
        Parse table = parseTable(tr("string", "s()"),
                tr("x", "${nonEmpty()}"));

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 0)));
        assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

        table = parseTable(tr("string", "s()"),
                tr("null", "${nonEmpty()}"));

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
        assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));
        assertThat(table.parts.more.more.parts.more.text(), containsString("!"));

        table = parseTable(
                tr("number", "n()"),
                tr("2", "${empty()}"));
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.counts().wrong, is(equalTo((Object) 1)));
        assertThat(numberObjFixture.counts().exceptions, is(equalTo((Object) 0)));
    }

    @Test
    public void testUpDown() {
        Parse table = parseTable(
                tr("number", "n()"),
                tr("1", "1"));
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.upCalled, is(true));
        assertThat(numberObjFixture.downCalled, is(true));
    }

    @Test
    public void testUpWithErrors() throws Exception {
        Parse table = parseTable(tr("x"));

        ColumnFixture fixture = new ColumnFixture() {
            @Override
            public void setUp() {
                throw new RuntimeException("x");
            }
        };
        fixture.doTable(table);

        assertThat(fixture.counts().right, is(equalTo((Object) 0)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
    }

    @Test
    public void testDownWithErrors() throws Exception {
        final Parse table = parseTable(tr("x"));

        ErrorFixture fixture = new ErrorFixture();
        fixture.doTable(table);

        assertThat(fixture.counts().right, is(equalTo((Object) 0)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
        assertThat(fixture.isDownCalled(), is(true));
    }

    @Test
    public void testGetParams() {
        stringObjFixture.setParams(new String[]{"x=y", "a=b"});

        assertThat(stringObjFixture.getParam("x"), is(equalTo("y")));
        assertThat(stringObjFixture.getParam("y"), is(nullValue()));

        assertThat(stringObjFixture.getParam("a", "z"), is(equalTo("b")));
        assertThat(stringObjFixture.getParam("u", "z"), is(equalTo("z")));
    }

    @Test
    public void testInit() {
        Parse table = parseTable(
                tr("number", "n()"),
                tr("1", "1"));

        numberObjFixture.setParams(new String[]{"testNr = 9"});
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.testNrAtStart, is(equalTo(9)));
    }

    @Test
    public void testSetValue() {
        Parse table = parseTable(
                tr("number", "n()"),
                tr("2", "${tests.put(x)}"),
                tr("${tests.get(x)}", "2"));
        numberObjFixture.doTable(table);
        assertThat(numberObjFixture.counts().right, is(equalTo((Object) 2)));
        assertThat(numberObjFixture.counts().exceptions, is(equalTo((Object) 0)));
    }
}
