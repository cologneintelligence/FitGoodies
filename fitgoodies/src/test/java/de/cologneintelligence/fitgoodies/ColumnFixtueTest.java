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


package de.cologneintelligence.fitgoodies;

import java.text.ParseException;

import org.jmock.lib.legacy.ClassImposteriser;

import de.cologneintelligence.fitgoodies.ColumnFixture;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import fit.Parse;

/**
 *
 * @author jwierum
 */
public class ColumnFixtueTest extends FitGoodiesTestCase {
    {
        setImposteriser(ClassImposteriser.INSTANCE);
    }

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
        public final void setUp() {
            upCalled = true;
            testNrAtStart = testNr;
        }
        @Override
        public final void tearDown() { downCalled = true; }
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

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        stringObjFixture = new StringObjFixture();
        numberObjFixture = new NumberObjFixture();
    }

    public final void testSimpleStringCases() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>x</td></tr></table>");
        stringObjFixture.doTable(table);
        assertEquals(1, stringObjFixture.counts.right);

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>y</td></tr></table>");
        stringObjFixture.doTable(table);
        assertEquals(1, stringObjFixture.counts.wrong);
    }

    public final void testSimpleNumberCase() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>2</td></tr></table>");
        numberObjFixture.doTable(table);
        assertEquals(1, numberObjFixture.counts.right);
    }

    public final void testCrossReferencesWithoutException() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>matched</td><td>${test}</td></tr></table>");

        CrossReferenceHelper helper = DependencyManager.getOrCreate(
                CrossReferenceHelper.class);
        helper.getProcessors().add(new CrossReferenceProcessorMock("test"));

        stringObjFixture.doTable(table);
        assertEquals(1, stringObjFixture.counts.right);

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>test2</td><td>${test}</td></tr></table>");
        stringObjFixture.doTable(table);
        assertEquals(1, stringObjFixture.counts.wrong);
    }

    public final void testCrossReferencesWithException() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>x</td><td>${nonEmpty()}</td></tr></table>");

        stringObjFixture.doTable(table);
        assertEquals(0, stringObjFixture.counts.wrong);
        assertEquals(1, stringObjFixture.counts.right);

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>string</td><td>s()</td></tr>"
                + "<tr><td>null</td><td>${nonEmpty()}</td></tr></table>");

        stringObjFixture.doTable(table);
        assertEquals(1, stringObjFixture.counts.wrong);
        assertEquals(1, stringObjFixture.counts.right);
        assertContains("!", table.parts.more.more.parts.more.text());

        table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>${empty()}</td></tr></table>");
        numberObjFixture.doTable(table);

        assertEquals(1, numberObjFixture.counts.wrong);
        assertEquals(0, numberObjFixture.counts.exceptions);
    }

    public final void testUpDown() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>1</td></tr>1</table>");
        numberObjFixture.doTable(table);

        assertTrue(numberObjFixture.upCalled);
        assertTrue(numberObjFixture.downCalled);
    }

    public final void testUpWithErrors() throws Exception {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>x</td></tr></table>");

        ColumnFixture fixture = new ColumnFixture() {
            @Override public void setUp() { throw new RuntimeException("x"); }
        };
        fixture.doTable(table);

        assertEquals(0, fixture.counts.right);
        assertEquals(0, fixture.counts.wrong);
        assertEquals(1, fixture.counts.exceptions);
    }

    public final void testDownWithErrors() throws Exception {
        final Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>x</td></tr></table>");

        ErrorFixture fixture = new ErrorFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.right);
        assertEquals(0, fixture.counts.wrong);
        assertEquals(1, fixture.counts.exceptions);
        assertTrue(fixture.isDownCalled());
    }

    public final void testGetParams() {
        stringObjFixture.setParams(new String[]{"x=y", "a=b"});

        assertEquals("y", stringObjFixture.getParam("x"));
        assertNull(stringObjFixture.getParam("y"));

        assertEquals("b", stringObjFixture.getParam("a", "z"));
        assertEquals("z", stringObjFixture.getParam("u", "z"));
    }

    public final void testInit() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>1</td></tr>1</table>");

        numberObjFixture.setParams(new String[]{"testNr = 9"});
        numberObjFixture.doTable(table);

        assertEquals(Integer.valueOf(9), numberObjFixture.testNrAtStart);
    }

    public final void testSetValue() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>number</td><td>n()</td></tr>"
                + "<tr><td>2</td><td>${tests.put(x)}</td></tr>"
                + "<tr><td>${tests.get(x)}</td><td>2</td></tr></table>");
        numberObjFixture.doTable(table);
        assertEquals(2, numberObjFixture.counts.right);
        assertEquals(0, numberObjFixture.counts.exceptions);
    }
}
