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
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public class BigDecimalParserTest extends FitGoodiesTestCase {
	@Test
	public void testParser() throws Exception {
		Parser<BigDecimal> p = new BigDecimalParser();

		assertThat(p.parse("42", null), is(equalTo(new BigDecimal("42"))));
		assertThat(p.parse("21", null), is(equalTo(new BigDecimal("21"))));
	}

	@Test
	public void testType() {
		assertThat(new BigDecimalParser().getType(), not(CoreMatchers.is(nullValue())));
	}
}
