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

import de.cologneintelligence.fitgoodies.ScientificDouble;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;



public class ParserHelperTest extends FitGoodiesTestCase {
    private ParserHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(ParserHelper.class);
    }

    @Test
    public void testRegister() throws Exception {
        assertThat(helper.parse("11", Integer.class, null), is(nullValue()));
        helper.registerParser(Integer.class, new Parser<Integer>() {
            @Override
            public Integer parse(final String s, final String i) { return Integer.parseInt(s); }
            @Override
            public Class<Integer> getType() { return Integer.class; }
        });

        Object expected = 42;
        Object actual = helper.parse("42", Integer.class, null);

        assertThat(actual, is(equalTo(expected)));
        assertThat(helper.parse("11", Long.class, null), is(nullValue()));

        helper.registerParser(new LongParserMock());
        expected = 23;
        actual = helper.parse("23", Integer.class, null);
        assertThat(actual, is(equalTo(expected)));

        actual = helper.parse("x", Long.class, null);
        assertThat(actual, is(equalTo((Object) (long) 2)));

        actual = helper.parse("x", Long.class, "y");
        assertThat(actual, is(equalTo((Object) (long) 7)));
    }

    @Test
    public void testDefaultRegisters() throws Exception {
        assertCanParse(BigInteger.class, "42");
        assertCanParse(BigDecimal.class, "3.14159");
        assertCanParse(Boolean.class, "true");
        assertCanParse(ScientificDouble.class, "1.5");
        assertCanParse(Object.class, "foo");
    }

    @Test
    public void unknownTypesMapToNull() throws Exception {
        assertThat(helper.parse("", StringBuffer.class, null), is(nullValue()));
    }

    public void assertCanParse(Class<?> targetClass, String value) throws Exception {
        assertThat(helper.parse(value, targetClass, null), not(CoreMatchers.is(nullValue())));
    }
}
