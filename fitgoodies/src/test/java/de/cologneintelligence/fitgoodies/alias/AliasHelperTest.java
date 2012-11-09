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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;

public final class AliasHelperTest extends FitGoodiesTestCase {
    private AliasHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = new AliasHelper();
    }

    public void testGetDefaultClass()  {
        String actual = helper.getClazz("java.lang.String");
        assertEquals("java.lang.String", actual);

        actual = helper.getClazz("java.lang.Integer");
        assertEquals("java.lang.Integer", actual);
    }

    public void testGetFitgoodiesMapping() {
        assertMapping("fitgoodies.ActionFixture");
        assertMapping("fitgoodies.ColumnFixture");
        assertMapping("fitgoodies.RowFixture");

        assertMapping("fitgoodies.file.FileFixture");
        assertMapping("fitgoodies.runners.RunFixture");
        assertMapping("fitgoodies.selenium.SeleniumFixture");
        assertMapping("fitgoodies.selenium.SetupFixture");
        assertMapping("fitgoodies.references.SetupFixture");
        assertMapping("fitgoodies.parsers.SetupFixture");
        assertMapping("fitgoodies.mail.SetupFixture");
        assertMapping("fitgoodies.date.SetupFixture");
        assertMapping("fitgoodies.database.SetupFixture");
        assertMapping("fitgoodies.adapters.SetupFixture");

        assertMapping("fitgoodies.database.ResultSetFixture");
        assertMapping("fitgoodies.database.TableFixture");

        assertMapping("fitgoodies.file.CSVFileRecordFixture");
        assertMapping("fitgoodies.file.DelimiterFileRecordFixture");
        assertMapping("fitgoodies.file.FixedLengthFileRecordFixture");
        assertMapping("fitgoodies.file.XMLFileFixture");
        assertMapping("fitgoodies.log4j.LogFixture");
        assertMapping("fitgoodies.log4j.SetupFixture");
        assertMapping("fitgoodies.mail.MailFixture");
        assertMapping("fitgoodies.alias.SetupFixture");
    }

    private void assertMapping(final String className) {
        final String actual = helper.getClazz(className);
        final String expected = "de.cologneintelligence." + className;
        assertEquals(expected, actual);
        try {
            assertNotNull(Class.forName(actual));
        } catch (final ClassNotFoundException e) {
            fail("The referenced fixture " + actual + " does not exist");
        }
    }

    public void testAddAlias() throws ClassNotFoundException {
        assertEquals("test", helper.getClazz("test"));

        helper.register("test", "fitgoodies.MyFixture");
        assertEquals("fitgoodies.MyFixture", helper.getClazz("test"));

        helper.register("test", "fitgoodies.MyActionFixture");
        assertEquals("fitgoodies.MyActionFixture", helper.getClazz("test"));

        assertEquals("test2", helper.getClazz("test2"));

        helper.register("test2", "fitgoodies.MyRowFixture");
        assertEquals("fitgoodies.MyRowFixture", helper.getClazz("test2"));
    }

    public void testRecursiveAliases() {
        helper.register("test", "test2");
        helper.register("test2", "test3");
        helper.register("test3", "ok");

        assertEquals("ok", helper.getClazz("test"));
    }
}
