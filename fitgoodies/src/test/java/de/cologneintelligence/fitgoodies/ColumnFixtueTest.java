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

import java.text.ParseException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ColumnFixtueTest extends FitGoodiesTestCase {
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
    public void testSimpleStringCases() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>x</td></tr></table>");
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.right, is(equalTo((Object) 1)));

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>y</td></tr></table>");
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.wrong, is(equalTo((Object) 1)));
    }

    @Test
    public void testSimpleNumberCase() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>2</td></tr></table>");
        numberObjFixture.doTable(table);
        assertThat(numberObjFixture.counts.right, is(equalTo((Object) 1)));
    }

    @Test
    public void testCrossReferencesWithoutException() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>matched</td><td>${test}</td></tr></table>");

        CrossReferenceHelper helper = DependencyManager.getOrCreate(
                CrossReferenceHelper.class);
        helper.getProcessors().add(new CrossReferenceProcessorMock("test"));

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.right, is(equalTo((Object) 1)));

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>test2</td><td>${test}</td></tr></table>");
        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.wrong, is(equalTo((Object) 1)));
    }

    @Test
    public void testCrossReferencesWithException() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>${nonEmpty()}</td></tr></table>");

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.wrong, is(equalTo((Object) 0)));
        assertThat(stringObjFixture.counts.right, is(equalTo((Object) 1)));

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>null</td><td>${nonEmpty()}</td></tr></table>");

        stringObjFixture.doTable(table);
        assertThat(stringObjFixture.counts.wrong, is(equalTo((Object) 1)));
        assertThat(stringObjFixture.counts.right, is(equalTo((Object) 1)));
        assertThat(table.parts.more.more.parts.more.text(), containsString("!"));

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>${empty()}</td></tr></table>");
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.counts.wrong, is(equalTo((Object) 1)));
        assertThat(numberObjFixture.counts.exceptions, is(equalTo((Object) 0)));
    }

    @Test
    public void testUpDown() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>1</td></tr>1</table>");
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.upCalled, is(true));
        assertThat(numberObjFixture.downCalled, is(true));
    }

    @Test
    public void testUpWithErrors() throws Exception {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>x</td></tr></table>");

        ColumnFixture fixture = new ColumnFixture() {
            @Override public void setUp() { throw new RuntimeException("x"); }
        };
        fixture.doTable(table);

        assertThat(fixture.counts.right, is(equalTo((Object) 0)));
        assertThat(fixture.counts.wrong, is(equalTo((Object) 0)));
        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
    }

    @Test
    public void testDownWithErrors() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>x</td></tr></table>");

        ErrorFixture fixture = new ErrorFixture();
        fixture.doTable(table);

        assertThat(fixture.counts.right, is(equalTo((Object) 0)));
        assertThat(fixture.counts.wrong, is(equalTo((Object) 0)));
        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
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
    public void testInit() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>1</td></tr>1</table>");

        numberObjFixture.setParams(new String[]{"testNr = 9"});
        numberObjFixture.doTable(table);

        assertThat(numberObjFixture.testNrAtStart, is(equalTo(9)));
    }

    @Test
    public void testSetValue() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>${tests.put(x)}</td></tr>"
                + "<tr><td>${tests.get(x)}</td><td>2</td></tr></table>");
        numberObjFixture.doTable(table);
        assertThat(numberObjFixture.counts.right, is(equalTo((Object) 2)));
        assertThat(numberObjFixture.counts.exceptions, is(equalTo((Object) 0)));
    }
}
