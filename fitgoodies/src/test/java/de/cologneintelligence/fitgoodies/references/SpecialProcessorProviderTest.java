/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.checker.DiagnosticChecker;
import de.cologneintelligence.fitgoodies.checker.ErrorChecker;
import de.cologneintelligence.fitgoodies.references.CellProcessor;
import de.cologneintelligence.fitgoodies.references.SpecialProcessorProvider;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SpecialProcessorProviderTest extends FitGoodiesTestCase {

	private SpecialProcessorProvider provider;

	@Before
	public void setUp() {
		provider = new SpecialProcessorProvider();
	}

	@Test
	public void testCanProcess() throws Exception {
		assertThat(provider.canProcess("error"), is(true));
		assertThat(provider.canProcess("eRRor"), is(true));
		assertThat(provider.canProcess(""), is(true));
		assertThat(provider.canProcess("other"), is(false));
	}

	@Test
	public void testCreate() throws Exception {
		CellProcessor processor = provider.create("error");
		assertThat(processor.getChecker(), is(instanceOf(ErrorChecker.class)));

		processor = provider.create("eRRor");
		assertThat(processor.getChecker(), is(instanceOf(ErrorChecker.class)));

		processor = provider.create("");
		assertThat(processor.getChecker(), is(instanceOf(DiagnosticChecker.class)));
	}
}
