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
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class ProcessorsTest extends FitGoodiesTestCase {
	private Processors procs;

	@Before
	public void setUp() throws Exception {
		procs = new Processors();
	}

	@Test
	public void testAdd() {
		procs.add(new CrossReferenceProcessorMock("x"));
		assertThat(procs.count(), is(equalTo((Object) 1)));
		procs.add(new CrossReferenceProcessorMock("y"));
		assertThat(procs.count(), is(equalTo((Object) 2)));
	}

	@Test
	public void testRemove() {
		AbstractCrossReferenceProcessor mock = new CrossReferenceProcessorMock("z");
		procs.add(new CrossReferenceProcessorMock("x"));
		procs.add(new CrossReferenceProcessorMock("y"));
		procs.add(mock);

		procs.remove(0);
		assertThat(procs.count(), is(equalTo((Object) 2)));

		procs.remove(mock);
		assertThat(procs.count(), is(equalTo((Object) 1)));
	}

	@Test
	public void testGet() {
		AbstractCrossReferenceProcessor mock = new CrossReferenceProcessorMock("z");

		procs.add(mock);
		assertThat(procs.get(0), is(sameInstance(mock)));

		procs.add(new CrossReferenceProcessorMock("y"));
		assertThat(procs.get(0), is(sameInstance(mock)));
		assertThat(mock, is(not(sameInstance(procs.get(1)))));
	}

	@Test
	public void testRegex() {
		procs.add(new CrossReferenceProcessorMock("x"));
		procs.add(new CrossReferenceProcessorMock("y"));

		Matcher m = procs.getSearchPattern().matcher("${x}");
		assertThat(m.find(), is(true));

		m = procs.getSearchPattern().matcher("a ${y} b");
		assertThat(m.find(), is(true));

		m = procs.getSearchPattern().matcher("a ${z} x");
		assertThat(m.find(), is(false));


		m = procs.getExtractPattern().matcher("${x}");
		assertThat(m.find(), is(true));

		m = procs.getExtractPattern().matcher("a ${y} b");
		assertThat(m.find(), is(false));

		m = procs.getExtractPattern().matcher("a ${z} x");
		assertThat(m.find(), is(false));

	}
}
