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


package fitgoodies.references;

import java.text.ParseException;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: SetupFixtureTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
	public final void testUse() throws ParseException {
		Processors procs = new Processors();
		SetupFixture setup = new SetupFixture(procs);

		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
				+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);
		assertSame(procs.get(0).getClass(),
				fitgoodies.references.processors.EmptyCrossReferenceProcessor.class);

		table = new Parse(
				"<table><tr><td>ignore</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
					+ "processors.StorageCrossReferenceProcessor</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
					+ "processors.StorageCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);
		assertSame(procs.get(1).getClass(),
				fitgoodies.references.processors.StorageCrossReferenceProcessor.class);
	}

	public final void testUseOutput() throws ParseException {
		Processors procs = new Processors();
		SetupFixture setup = new SetupFixture(procs);

		Parse table = new Parse(
				"<table><tr><td>ignore</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
					+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);
		assertContains("nonEmpty", table.parts.more.parts.more.text());
	}

	public final void testRemove() throws ParseException {
		Processors procs = new Processors();
		SetupFixture setup = new SetupFixture(procs);

		Parse table = new Parse("<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
					+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
					+ "processors.StorageCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);

		table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>remove</td><td>fitgoodies.references."
					+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);

		assertEquals(1, procs.count());

		table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>remove</td><td>fitgoodies.references."
					+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);

		assertEquals(1, procs.count());
	}

	public final void testConstructor() throws ParseException  {
		while (CrossReferenceHelper.instance().getProcessors().count() > 0) {
			CrossReferenceHelper.instance().getProcessors().remove(0);
		}

		SetupFixture setup = new SetupFixture();

		Parse table = new Parse(
				"<table><tr><td>ignore</td></tr>"
				+ "<tr><td>use</td><td>fitgoodies.references."
				+ "processors.EmptyCrossReferenceProcessor</td></tr>"
				+ "</table>");
		setup.doTable(table);
		assertEquals(1, CrossReferenceHelper.instance().getProcessors().count());
	}
}
