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
import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.Parse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
	@Test
	public void testSetup() throws Exception {
		Fixture fixture = new Fixture();

		try {
			assertThat(fixture.parse("42", Long.class), is(nullValue()));
			Assert.fail();
		} catch(IllegalArgumentException ignore) {}

		registerLongParser();
		assertThat(fixture.parse("42", Long.class), not(CoreMatchers.is(nullValue())));
	}

	public void registerLongParser() {
		final Fixture fixture = new SetupFixture();
		final Parse table = parseTable(tr("load", "de.cologneintelligence.fitgoodies.parsers.LongParserMock"));
		fixture.doTable(table);
	}
}
