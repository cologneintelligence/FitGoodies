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


package de.cologneintelligence.fitgoodies.references;

import java.text.ParseException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

/**
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
    public final void testUse() throws ParseException {
        final Processors procs = new Processors();
        final SetupFixture setup = new SetupFixture(procs);

        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                + "processors.EmptyCrossReferenceProcessor</td></tr>"
                + "</table>");
        setup.doTable(table);
        assertSame(procs.get(0).getClass(),
                de.cologneintelligence.fitgoodies.references.processors.EmptyCrossReferenceProcessor.class);

        table = new Parse(
                "<table><tr><td>ignore</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.StorageCrossReferenceProcessor</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.StorageCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);
        assertSame(procs.get(1).getClass(),
                de.cologneintelligence.fitgoodies.references.processors.StorageCrossReferenceProcessor.class);
    }

    public final void testUseOutput() throws ParseException {
        final Processors procs = new Processors();
        final SetupFixture setup = new SetupFixture(procs);

        final Parse table = new Parse(
                "<table><tr><td>ignore</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);
        assertContains("nonEmpty", table.parts.more.parts.more.text());
    }

    public final void testRemove() throws ParseException {
        final Processors procs = new Processors();
        final SetupFixture setup = new SetupFixture(procs);

        Parse table = new Parse("<table>"
                + "<tr><td>ignore</td></tr>"
                + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                + "processors.EmptyCrossReferenceProcessor</td></tr>"
                + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                + "processors.StorageCrossReferenceProcessor</td></tr>"
                + "</table>");
        setup.doTable(table);

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>remove</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);

        assertEquals(1, procs.count());

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>remove</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);

        assertEquals(1, procs.count());
    }

    public final void testConstructor() throws ParseException  {
        CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);

        while (helper.getProcessors().count() > 0) {
            helper.getProcessors().remove(0);
        }

        final SetupFixture setup = new SetupFixture();

        final Parse table = new Parse(
                "<table><tr><td>ignore</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);
        assertEquals(1, helper.getProcessors().count());
    }
}
