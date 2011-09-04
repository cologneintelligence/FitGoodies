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


package de.cologneintelligence.fitgoodies.parsers;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.parsers.Parser;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;


/**
 * $Id$
 * @author jwierum
 */
public class ParserHelperTest extends FitGoodiesTestCase {
	public final void testSingleton() {
		ParserHelper expected = ParserHelper.instance();
		assertSame(expected, ParserHelper.instance());

		ParserHelper.reset();
		assertNotSame(expected, ParserHelper.instance());
	}

	public final void testRegister() throws Exception {
		assertNull(ParserHelper.instance().parse("11", Integer.class, null));
		ParserHelper.instance().registerParser(Integer.class, new Parser<Integer>() {
			public Integer parse(final String s, final String i) { return Integer.parseInt(s); }
			public Class<Integer> getType() { return Integer.class; }
		});

		Object expected = Integer.valueOf(42);
		Object actual = ParserHelper.instance().parse("42", Integer.class, null);

		assertEquals(expected, actual);
		assertNull(ParserHelper.instance().parse("11", Long.class, null));

		ParserHelper.instance().registerParser(new LongParserMock());
		expected = Integer.valueOf(23);
		actual = ParserHelper.instance().parse("23", Integer.class, null);
		assertEquals(expected, actual);

		actual = ParserHelper.instance().parse("x", Long.class, null);
		expected = Long.valueOf(2);
		assertEquals(expected, actual);

		actual = ParserHelper.instance().parse("x", Long.class, "y");
		expected = Long.valueOf(7);
		assertEquals(expected, actual);
	}

	public final void testDefaultRegisters() throws Exception {
		assertNotNull(ParserHelper.instance().parse("42", BigInteger.class, null));
		assertNotNull(ParserHelper.instance().parse("3.14159", BigDecimal.class, null));
	}
}
