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
import java.util.regex.Matcher;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.Processors;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;


/**
 * $Id$
 * @author jwierum
 *
 */
public class ProcessorsTest extends FitGoodiesTestCase {
	private Processors procs;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		procs = new Processors();
	}

	public final void testAdd() {
		procs.add(new CrossReferenceProcessorMock("x"));
		assertEquals(1, procs.count());
		procs.add(new CrossReferenceProcessorMock("y"));
		assertEquals(2, procs.count());
	}

	public final void testRemove() {
		AbstractCrossReferenceProcessor mock = new CrossReferenceProcessorMock("z");
		procs.add(new CrossReferenceProcessorMock("x"));
		procs.add(new CrossReferenceProcessorMock("y"));
		procs.add(mock);

		procs.remove(0);
		assertEquals(2, procs.count());

		procs.remove(mock);
		assertEquals(1, procs.count());
	}

	public final void testGet() {
		AbstractCrossReferenceProcessor mock = new CrossReferenceProcessorMock("z");

		procs.add(mock);
		assertSame(mock, procs.get(0));

		procs.add(new CrossReferenceProcessorMock("y"));
		assertSame(mock, procs.get(0));
		assertNotSame(mock, procs.get(1));
	}

	public final void testRegex() {
		procs.add(new CrossReferenceProcessorMock("x"));
		procs.add(new CrossReferenceProcessorMock("y"));

		Matcher m = procs.getSearchPattern().matcher("${x}");
		assertTrue(m.find());

		m = procs.getSearchPattern().matcher("a ${y} b");
		assertTrue(m.find());

		m = procs.getSearchPattern().matcher("a ${z} x");
		assertFalse(m.find());


		m = procs.getExtractPattern().matcher("${x}");
		assertTrue(m.find());

		m = procs.getExtractPattern().matcher("a ${y} b");
		assertFalse(m.find());

		m = procs.getExtractPattern().matcher("a ${z} x");
		assertFalse(m.find());
	}
}
