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
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectParserTest extends FitGoodiesTestCase {
	@Test
	public void testGetType() {
		Parser<Object> p = new ObjectParser();
		assertThat(p.getType(), is(equalTo(Object.class)));
	}

	@Test
	public void testParse() throws Exception {
		Parser<Object> p = new ObjectParser();
		assertThat(p.parse("test", null), (Matcher) is(equalTo("test")));
		assertThat(p.parse("string", null), (Matcher) is(equalTo("string")));
	}
}