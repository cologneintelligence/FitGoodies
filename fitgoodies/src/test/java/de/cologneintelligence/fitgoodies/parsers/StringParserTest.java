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

package de.cologneintelligence.fitgoodies.parsers;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringParserTest extends FitGoodiesTestCase {

	@Test
	public void testParse() throws Exception {
		assertThat(new StringParser().parse("test", null), is(equalTo("test")));
		assertThat(new StringParser().parse("value", null), is(equalTo("value")));
	}

	@Test
	public void parseIsTheIdentityFunction() throws Exception {
		String x = "an object";
		assertThat(new StringParser().parse(x, null), is(sameInstance(x)));
	}

	@Test
	public void testDatatype() {
		assertThat(new StringParser().getType(), is(equalTo(String.class)));
	}

}
