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


package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.DummyTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.parsers.LongParserMock;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class FixtureToolsTest extends FitGoodiesTestCase {
    public static class DummyValueFixture extends Fixture {
        public int x;
        public String y;

        public int a;
        public String b;

        public BigInteger[] arr = new BigInteger[]{
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3")};
    }

    @Test
    public void testParse() throws Exception {
        final ParserHelper helper = new ParserHelper();
        final Integer intExpected = 42;
        assertThat(FixtureTools.parse("42", intExpected.getClass(), null, helper), is(nullValue()));

        BigInteger biExpected = new BigInteger("123");
        assertThat(FixtureTools.parse("123", biExpected.getClass(), null, helper), (Matcher) is(equalTo(biExpected)));
        biExpected = new BigInteger("7");
        assertThat(FixtureTools.parse("7", biExpected.getClass(), null, helper), (Matcher) is(equalTo(biExpected)));

        BigDecimal bdExpected = new BigDecimal("312.45");
        assertThat(FixtureTools.parse("312.45", bdExpected.getClass(), null, helper), (Matcher) is(equalTo(bdExpected)));
        bdExpected = new BigDecimal("331.0");
        assertThat(FixtureTools.parse("331.0", bdExpected.getClass(), null, helper), (Matcher) is(equalTo(bdExpected)));

        helper.registerParser(new LongParserMock());
        assertThat(FixtureTools.parse("7", Long.class, "x", helper), (Matcher) is(equalTo((long) 7)));
    }

    @Test
    public void testRebindTypeAdapter() {
        final TypeAdapter ta = new TypeAdapter();
        final TypeAdapterHelper helper = new TypeAdapterHelper();
        TypeAdapter actual;

        ta.type = BigInteger.class;
        actual = FixtureTools.rebindTypeAdapter(ta, null, helper);
        assertThat(actual, is(sameInstance(ta)));

        helper.register(DummyTypeAdapter.class);

        actual = FixtureTools.rebindTypeAdapter(ta, null, helper);
        assertThat(ta, is(not(sameInstance(actual))));
        assertThat(actual, is(instanceOf(DummyTypeAdapter.class)));
    }

    @Test
    public void testRebingTypeAdapterWithParameter() throws Exception {
        final TypeAdapter ta = new TypeAdapter();
        AbstractTypeAdapter<?> actual;

        ta.type = BigInteger.class;
        final TypeAdapterHelper helper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
        helper.register(DummyTypeAdapter.class);

        actual = (AbstractTypeAdapter<?>)
                FixtureTools.rebindTypeAdapter(ta, "test", helper);
        assertThat(actual.getParameter(), is(equalTo("test")));

        actual = (AbstractTypeAdapter<?>)
                FixtureTools.rebindTypeAdapter(ta, "parameter", helper);
        assertThat(actual.getParameter(), is(equalTo("parameter")));
    }

    @Test
    public void testGetParameter() throws Exception {
        final CrossReferenceHelper helper = new CrossReferenceHelper();
        String[] args = new String[]{
                "x = y", " param = value "
        };

        assertThat(FixtureTools.getArg(args, "x", null, helper), is(equalTo("y")));
        assertThat(FixtureTools.getArg(args, "param", null, helper), is(equalTo("value")));
        assertThat(FixtureTools.getArg(args, "not-good", "good", helper), is(equalTo("good")));


        args = new String[]{
                "x =z", " a b=test "
        };

        assertThat(FixtureTools.getArg(args, "param", "bad", helper), is(equalTo("bad")));
        assertThat(FixtureTools.getArg(args, "X", null, helper), is(equalTo("z")));
        assertThat(FixtureTools.getArg(args, "A B", null, helper), is(equalTo("test")));

        assertThat(FixtureTools.getArg(null, "x", "null", helper), is(equalTo("null")));
        assertThat(FixtureTools.getArg(null, "y", "error", helper), is(equalTo("error")));

        args = new String[]{
                "y = a${tests.get(x)}b", " a b=test "
        };

        helper.parseBody("${tests.put(x)}", "x");
        assertThat(FixtureTools.getArg(args, "y", null, helper), is(equalTo("axb")));
    }

    @Test
    public void testGetParameters() {
        String[] args = new String[]{
                "x = y", " param = value "
        };

        String[] actual = FixtureTools.getArgs(args);
        assertThat(actual.length, is(equalTo((Object) 2)));
        assertThat(actual[0], is(equalTo("x")));
        assertThat(actual[1], is(equalTo("param")));


        args = new String[]{
                "x =z", " a b=test "
        };

        actual = FixtureTools.getArgs(args);
        assertThat(actual.length, is(equalTo((Object) 2)));
        assertThat(actual[0], is(equalTo("x")));
        assertThat(actual[1], is(equalTo("a b")));

        actual = FixtureTools.getArgs(null);
        assertThat(actual.length, is(equalTo((Object) 0)));

        actual = FixtureTools.getArgs(new String[]{});
        assertThat(actual.length, is(equalTo((Object) 0)));
    }


    @Test
    public void testResolveQuestionMarks() throws Exception {
        Parse table = parseTableWithoutAnnotation(
                tr("ok", "good()", "works?", "y?n"),
                tr("alone?"));

        FixtureTools.resolveQuestionMarks(table.parts);
        assertThat(table.parts.parts.text(), is(equalTo("ok")));
        assertThat(table.parts.parts.more.text(), is(equalTo("good()")));
        assertThat(table.parts.parts.more.more.text(), is(equalTo("works()")));
        assertThat(table.parts.parts.more.more.more.text(), is(equalTo("y?n")));
        assertThat(table.parts.more.parts.text(), is(equalTo("alone?")));

        table = parseTableWithoutAnnotation(
                tr("alone?", "ok()", "x"),
                tr("works?"));

        FixtureTools.resolveQuestionMarks(table.parts);
        assertThat(table.parts.parts.text(), is(equalTo("alone()")));
        assertThat(table.parts.parts.more.text(), is(equalTo("ok()")));
        assertThat(table.parts.parts.more.more.text(), is(equalTo("x")));
        assertThat(table.parts.more.parts.text(), is(equalTo("works?")));
    }

    @Test
    public void testCopyParamsToFixture() {
        final CrossReferenceHelper crHelper = new CrossReferenceHelper();
        final TypeAdapterHelper taHelper = new TypeAdapterHelper();

        DummyValueFixture fixture = new DummyValueFixture();
        String[] args = new String[]{" x = 8 ", "y=string", "z=error"};

        fixture.a = 9;
        FixtureTools.copyParamsToFixture(args, fixture, crHelper, taHelper);
        assertThat(fixture.a, is(equalTo((Object) 9)));
        assertThat(fixture.x, is(equalTo((Object) 8)));
        assertThat(fixture.y, is(equalTo("string")));

        fixture = new DummyValueFixture();
        args = new String[]{" a = 42 ", "b=c"};

        FixtureTools.copyParamsToFixture(args, fixture, crHelper, taHelper);
        assertThat(fixture.a, is(equalTo((Object) 42)));
        assertThat(fixture.b, is(equalTo("c")));
    }

    @Test
    public void testColumnParameters() throws Exception {
        Parse table = parseTableWithoutAnnotation(
                tr("x[1 2]", "y[3 4]", "z"),
                tr("a[7]", "b", "c"));

        String[] actual = FixtureTools.extractColumnParameters(table.parts);
        assertThat(Arrays.asList("1 2", "3 4", null), is(equalTo(Arrays.asList(actual))));
        assertThat(table.parts.parts.text(), is(equalTo("x")));
        assertThat(table.parts.parts.more.text(), is(equalTo("y")));
        assertThat(table.parts.more.parts.text(), is(equalTo("a[7]")));

        table = parseTableWithoutAnnotation(tr("name", "date [ de_DE, dd.MM.yyyy ] "));

        actual = FixtureTools.extractColumnParameters(table.parts);
        assertThat(Arrays.asList(null, "de_DE, dd.MM.yyyy"), is(equalTo(Arrays.asList(actual))));
        assertThat(table.parts.parts.text(), is(equalTo("name")));
        assertThat(table.parts.parts.more.text(), is(equalTo("date")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertBoolean() {
        assertThat(FixtureTools.convertToBoolean("TrUe"), is(true));
        assertThat(FixtureTools.convertToBoolean("1"), is(true));
        assertThat(FixtureTools.convertToBoolean("yes"), is(true));
        assertThat(FixtureTools.convertToBoolean("false"), is(false));

        assertThat(FixtureTools.convertToBoolean("no"), is(false));

        assertThat(FixtureTools.convertToBoolean("0"), is(false));

        FixtureTools.convertToBoolean("non-bool");
    }
}
