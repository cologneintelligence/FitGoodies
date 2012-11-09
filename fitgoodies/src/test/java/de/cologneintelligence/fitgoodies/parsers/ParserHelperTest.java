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
import de.cologneintelligence.fitgoodies.util.DependencyManager;


/**
 * @author jwierum
 */
public class ParserHelperTest extends FitGoodiesTestCase {
    private ParserHelper helper;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.INSTANCE.getOrCreate(ParserHelper.class);
    }

    public final void testRegister() throws Exception {
        assertNull(helper.parse("11", Integer.class, null));
        helper.registerParser(Integer.class, new Parser<Integer>() {
            @Override
            public Integer parse(final String s, final String i) { return Integer.parseInt(s); }
            @Override
            public Class<Integer> getType() { return Integer.class; }
        });

        Object expected = Integer.valueOf(42);
        Object actual = helper.parse("42", Integer.class, null);

        assertEquals(expected, actual);
        assertNull(helper.parse("11", Long.class, null));

        helper.registerParser(new LongParserMock());
        expected = Integer.valueOf(23);
        actual = helper.parse("23", Integer.class, null);
        assertEquals(expected, actual);

        actual = helper.parse("x", Long.class, null);
        expected = Long.valueOf(2);
        assertEquals(expected, actual);

        actual = helper.parse("x", Long.class, "y");
        expected = Long.valueOf(7);
        assertEquals(expected, actual);
    }

    public final void testDefaultRegisters() throws Exception {
        assertNotNull(helper.parse("42", BigInteger.class, null));
        assertNotNull(helper.parse("3.14159", BigDecimal.class, null));
    }
}
