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


package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
    @Test
    public void testUse() throws ParseException {
        final Processors procs = new Processors();
        final SetupFixture setup = new SetupFixture(procs);

        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                + "processors.EmptyCrossReferenceProcessor</td></tr>"
                + "</table>");
        setup.doTable(table);
        assertThat(de.cologneintelligence.fitgoodies.references.processors.EmptyCrossReferenceProcessor.class, (Matcher) is(sameInstance(procs.get(0).getClass())));

        table = new Parse(
                "<table><tr><td>ignore</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.StorageCrossReferenceProcessor</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.StorageCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);
        assertThat(de.cologneintelligence.fitgoodies.references.processors.StorageCrossReferenceProcessor.class, (Matcher) is(sameInstance(procs.get(1).getClass())));
    }

    @Test
    public void testUseOutput() throws ParseException {
        final Processors procs = new Processors();
        final SetupFixture setup = new SetupFixture(procs);

        final Parse table = new Parse(
                "<table><tr><td>ignore</td></tr>"
                        + "<tr><td>use</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);
        assertThat(table.parts.more.parts.more.text(), containsString("nonEmpty"));
    }

    @Test
    public void testRemove() throws ParseException {
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

        assertThat(procs.count(), is(equalTo((Object) 1)));

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>remove</td><td>de.cologneintelligence.fitgoodies.references."
                        + "processors.EmptyCrossReferenceProcessor</td></tr>"
                        + "</table>");
        setup.doTable(table);

        assertThat(procs.count(), is(equalTo((Object) 1)));
    }

    @Test
    public void testConstructor() throws ParseException  {
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
        assertThat(helper.getProcessors().count(), is(equalTo((Object) 1)));
    }
}