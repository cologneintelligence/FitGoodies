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


package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class StorageCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new StorageCrossReferenceProcessor();
    }

    @Test
    public void testPattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${ns.get(one)}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${ns.get(two)}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${x.get(three)}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${xy.put(four)}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${six.containsValue(five)}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${six.containsValue(five, /a\\/(b)\\/c/)}");
        assertThat(m.find(), is(true));
    }

    @Test
    public void testNegativePattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${six.containsValue()}");
        assertThat(m.find(), is(false));

        m = regex.matcher("${.put(x)}");
        assertThat(m.find(), is(false));

        m = regex.matcher("${put(x)}");
        assertThat(m.find(), is(false));

        m = regex.matcher("${x.putput(y)}");
        assertThat(m.find(), is(false));

        m = regex.matcher("${x.put(y, /a/b/)}");
        assertThat(m.find(), is(false));

    }

    private void checkCr(final CrossReference cr, final String cmd,
            final String namespace, final String parameter) {
        assertThat(cr, not(is(nullValue())));
        assertThat(cr.getCommand(), is(equalTo(cmd)));
        assertThat(cr.getNamespace(), is(equalTo(namespace)));
        assertThat(cr.getParameter(), is(equalTo(parameter)));
        assertThat(cr.getProcessor(), is(sameInstance(processor)));
    }

    @Test
    public void testExtraction() {
        CrossReference cr;

        cr = processor.extractCrossReference("ns1.put(x)");
        checkCr(cr, "put", "ns1", "x");

        cr = processor.extractCrossReference("n.put(x3)");
        checkCr(cr, "put", "n", "x3");

        cr = processor.extractCrossReference("ns3.get(param)");
        checkCr(cr, "get", "ns3", "param");

        cr = processor.extractCrossReference("n.containsValue(param)");
        checkCr(cr, "containsValue", "n", "param");

        assertThat(processor.extractCrossReference("ns3.err(param)"), is(nullValue()));
    }

    public void checkProcessing(
            final String cmd, final String ns, final String param,
            final Object object, final String expected)
                    throws CrossReferenceProcessorShortcutException {

        String actual = processor.processMatch(
                new CrossReference(cmd, ns, param, null), object);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void testProcessing()
            throws CrossReferenceProcessorShortcutException {

        checkProcessing("put", "one", "x", 42, "42");
        checkProcessing("get", "one", "x", 21, "42");
        checkProcessing("put", "one", "x", new StringBuilder("two"), "two");
        checkProcessing("get", "one", "x", null, "two");


        checkProcessing("get", "two", "n", null,
                "two.n: cross reference could not be resolved!");

        checkProcessing("containsValue", "two", "z", "good",
                "two.z: no value found!");
        checkProcessing("containsValue", "one", "x", "good",
                "good");
    }

    @Test
    public void testRegexExtraction()
            throws CrossReferenceProcessorShortcutException {

        checkProcessing("put", "retest", "val1, /my\\/([^\\/]+)\\/test/",
                "my/simple/test", "my/simple/test");
        checkProcessing("get", "retest", "val1",
                "x", "simple");

        checkProcessing("put", "retest", "val2, /a(b)c/",
                "abc", "abc");
        checkProcessing("get", "retest", "val2",
                "x", "b");

        checkProcessing("put", "retest", "val3, /a/c/",
                "abc", "/a/c/: illegal regex");
        checkProcessing("get", "retest", "val3", null,
                "retest.val3: cross reference could not be resolved!");
    }
}
