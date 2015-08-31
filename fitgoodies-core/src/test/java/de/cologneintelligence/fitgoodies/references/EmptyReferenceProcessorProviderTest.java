/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.checker.Checker;
import de.cologneintelligence.fitgoodies.checker.EmptyChecker;
import de.cologneintelligence.fitgoodies.checker.NullChecker;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class EmptyReferenceProcessorProviderTest extends FitGoodiesTestCase {
	private EmptyReferenceProcessorProvider provider;

	@Before
	public void setUp() throws Exception {
		provider = new EmptyReferenceProcessorProvider();
	}

	@Test
	public void testPattern() {
		assertThat(provider.canProcess("${null}"), is(true));
		assertThat(provider.canProcess("$null"), is(true));
		assertThat(provider.canProcess("${nuLL}"), is(true));
		assertThat(provider.canProcess("${notnull}"), is(true));
		assertThat(provider.canProcess("${empty}"), is(true));
		assertThat(provider.canProcess("${nonEmpty}"), is(true));
		assertThat(provider.canProcess("${nonEmpty()}"), is(true));
		assertThat(provider.canProcess("${notEmpty()}"), is(true));
		assertThat(provider.canProcess("$notEmpty"), is(true));
		assertThat(provider.canProcess("${null()}"), is(true));

		assertThat(provider.canProcess("${bla}"), is(false));
		assertThat(provider.canProcess("null()"), is(false));
	}

	@Test
	public void providersReturnCustomChecker() {
		Checker checker = provider.create("${null}").getChecker();
		assertThat(checker, is(instanceOf(NullChecker.class)));
		assertThat(((NullChecker) checker).isExpectEmpty(), is(true));

		checker = provider.create("${notnull}").getChecker();
		assertThat(checker, is(instanceOf(NullChecker.class)));
		assertThat(((NullChecker) checker).isExpectEmpty(), is(false));

		checker = provider.create("${empty}").getChecker();
		assertThat(checker, is(instanceOf(EmptyChecker.class)));
		assertThat(((EmptyChecker) checker).isExpectEmpty(), is(true));

		checker = provider.create("${nonempty}").getChecker();
		assertThat(checker, is(instanceOf(EmptyChecker.class)));
		assertThat(((EmptyChecker) checker).isExpectEmpty(), is(false));
	}

	@Test
	public void processorsPreprocessNull() {
        assertThat(provider.create("a ${null} b").preprocess(), is(equalTo("a  b")));
    }

	@Test
	public void willReturnAChecker() {
		assertThat(provider.create("$null").replacesCheckRoutine(), is(true));
	}

}
