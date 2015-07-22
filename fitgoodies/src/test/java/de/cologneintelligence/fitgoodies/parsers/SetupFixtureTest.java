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


package de.cologneintelligence.fitgoodies.parsers;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Fixture;
import fit.Parse;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


public class SetupFixtureTest extends FitGoodiesTestCase {
	@Test
	public void testSetup() throws Exception {
	    final ParserHelper helper = DependencyManager.getOrCreate(ParserHelper.class);

		assertThat(FixtureTools.parse("42", Long.class, null, helper), is(nullValue()));
		final Fixture fixture = new SetupFixture();

		final Parse table = parseTable(
				tr("load", "de.cologneintelligence.fitgoodies.parsers.LongParserMock"));

		fixture.doTable(table);
		assertThat(FixtureTools.parse("42", Long.class, null, helper), not(CoreMatchers.is(nullValue())));
	}
}
