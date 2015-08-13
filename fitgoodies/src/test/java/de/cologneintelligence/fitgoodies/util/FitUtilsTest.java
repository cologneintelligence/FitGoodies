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

package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FitUtilsTest extends FitGoodiesTestCase {
	@Test
	public void testEscape() {
		assertThat(FitUtils.escape("     "), is(equalTo(" &nbsp; &nbsp; ")));

		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertThat(FitUtils.escape(junk), is(equalTo(junk)));
		assertThat(FitUtils.escape(""), is(equalTo("")));
		assertThat(FitUtils.escape("<"), is(equalTo("&lt;")));
		assertThat(FitUtils.escape("<<"), is(equalTo("&lt;&lt;")));
		assertThat(FitUtils.escape("x<"), is(equalTo("x&lt;")));
		assertThat(FitUtils.escape("&"), is(equalTo("&amp;")));
		assertThat(FitUtils.escape("<&<"), is(equalTo("&lt;&amp;&lt;")));
		assertThat(FitUtils.escape("&<&"), is(equalTo("&amp;&lt;&amp;")));
		assertThat(FitUtils.escape("a < b && c < d"), is(equalTo("a &lt; b &amp;&amp; c &lt; d")));
		assertThat(FitUtils.escape("a\nb"), is(equalTo("a<br />b")));
	}

	@Test
	public void testExtractCellParameter() throws Exception {
		Parse cell1 = parseTd("empty");
		Parse cell2 = parseTd("empty[hello]");
		Parse cell3 = parseTd("some text [ some information ]");

		String parameter1 = FitUtils.extractCellParameter(cell1);
		String parameter2 = FitUtils.extractCellParameter(cell2);
		String parameter3 = FitUtils.extractCellParameter(cell3);

		assertThat(parameter1, is(nullValue()));
		assertThat(parameter2, is(equalTo("hello")));
		assertThat(parameter3, is(equalTo("some information")));

		assertThat(cell1.text(), is(equalTo("empty")));
		assertThat(cell2.text(), is(equalTo("empty")));
		assertThat(cell3.text(), is(equalTo("some text")));
	}

	@Test
	public void testExtractCellParameterMap() throws Exception {
		Parse cell1 = parseTd("empty");
		Parse cell2 = parseTd("empty[hello=world]");
		Parse cell3 = parseTd("some text [some=information, ignore me  , a  =  message ]");

		Map<String, String> parameter1 = FitUtils.extractCellParameterMap(cell1);
		Map<String, String> parameter2 = FitUtils.extractCellParameterMap(cell2);
		Map<String, String> parameter3 = FitUtils.extractCellParameterMap(cell3);

		Map<String, String> expectedParameter2 = new HashMap<>();
		expectedParameter2.put("hello", "world");

		Map<String, String> expectedParameter3 = new HashMap<>();
		expectedParameter3.put("some", "information");
		expectedParameter3.put("a", "message");

		assertThat(parameter1.entrySet(), is(empty()));
		assertThat(parameter2, is(equalTo(expectedParameter2)));
		assertThat(parameter3, is(equalTo(expectedParameter3)));

		assertThat(cell1.text(), is(equalTo("empty")));
		assertThat(cell2.text(), is(equalTo("empty")));
		assertThat(cell3.text(), is(equalTo("some text")));
	}
}
