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

package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class BigIntegerTypeHandlerTest extends FitGoodiesTestCase {

	private BigIntegerTypeHandler handler;

	@Before
	public void setUp() {
		handler = new BigIntegerTypeHandler(null);
	}

	@Test
	public void testParse() throws Exception {
		assertThat(handler.unsafeParse("42"), is(equalTo(new BigInteger("42"))));
		assertThat(handler.unsafeParse("21"), is(equalTo(new BigInteger("21"))));
	}

	@Test
	public void testToString() {
		assertThat(handler.toString(new BigInteger("12")), is(equalTo("12")));
		assertThat(handler.toString(new BigInteger("53")), is(equalTo("53")));
	}

	@Test
	public void testType() {
		assertThat(handler.getType(), equalTo(BigInteger.class));
	}
}
