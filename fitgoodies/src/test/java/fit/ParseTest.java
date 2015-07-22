package fit;

//Copyright (c) 2002 Cunningham & Cunningham, Inc.
//Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParseTest extends FitGoodiesTestCase {

	@Test
	public void testParsing() throws Exception {
		Parse p = new Parse("leader<Table foo=2>body</table>trailer", new String[]{"table"});
		assertThat(p.leader, is(equalTo("leader")));
		assertThat(p.tag, is(equalTo("<Table foo=2>")));
		assertThat(p.body, is(equalTo("body")));
		assertThat(p.trailer, is(equalTo("trailer")));
	}

	@Test
	public void testRecursing() {
		Parse p = parse("leader<table><TR><Td>body</tD></TR></table>trailer");
		assertThat(p.body, is(equalTo(null)));
		assertThat(p.parts.body, is(equalTo(null)));
		assertThat(p.parts.parts.body, is(equalTo("body")));
	}

	@Test
	public void testIterating() {
		Parse p = parse("leader<table><tr><td>one</td><td>two</td><td>three</td></tr></table>trailer");
		assertThat(p.parts.parts.body, is(equalTo("one")));
		assertThat(p.parts.parts.more.body, is(equalTo("two")));
		assertThat(p.parts.parts.more.more.body, is(equalTo("three")));
	}

	@Test
	public void testIndexing() {
		Parse p = parse("leader<table><tr><td>one</td><td>two</td><td>three</td></tr><tr><td>four</td></tr></table>trailer");
		assertThat(p.at(0, 0, 0).body, is(equalTo("one")));
		assertThat(p.at(0, 0, 1).body, is(equalTo("two")));
		assertThat(p.at(0, 0, 2).body, is(equalTo("three")));
		assertThat(p.at(0, 0, 3).body, is(equalTo("three")));
		assertThat(p.at(0, 0, 4).body, is(equalTo("three")));
		assertThat(p.at(0, 1, 0).body, is(equalTo("four")));
		assertThat(p.at(0, 1, 1).body, is(equalTo("four")));
		assertThat(p.at(0, 2, 0).body, is(equalTo("four")));
		assertThat(p.size(), is(equalTo((Object) 1)));
		assertThat(p.parts.size(), is(equalTo((Object) 2)));
		assertThat(p.parts.parts.size(), is(equalTo((Object) 3)));
		assertThat(p.leaf().body, is(equalTo("one")));
		assertThat(p.parts.last().leaf().body, is(equalTo("four")));
	}

	@Test
	public void testParseException() {
		try {
			new Parse("leader<table><tr><th>one</th><th>two</th><th>three</th></tr><tr><td>four</td></tr></table>trailer");
		} catch (java.text.ParseException e) {
			assertThat(e.getErrorOffset(), is(equalTo((Object) 17)));
			assertThat(e.getMessage(), is(equalTo("Can't find tag: td")));
			return;
		}
		Assert.fail("exptected exception not thrown");
	}

	@Test
	public void testText() {
		Parse p = parseTd("a&lt;b");
		assertThat(p.body, is(equalTo("a&lt;b")));
		assertThat(p.text(), is(equalTo("a<b")));
		p = parseTd("\ta&gt;b&nbsp;&amp;&nbsp;b>c &&&lt;");
		assertThat(p.text(), is(equalTo("a>b & b>c &&<")));
		p = parseTd("\ta&gt;b&nbsp;&amp;&nbsp;b>c &&lt;");
		assertThat(p.text(), is(equalTo("a>b & b>c &<")));
		p = parseTd("<P><FONT FACE=\"Arial\" SIZE=2>GroupTestFixture</FONT>");
		assertThat(p.text(), is(equalTo("GroupTestFixture")));

		assertThat(Parse.htmlToText("&nbsp;"), is(equalTo("")));
		assertThat(Parse.htmlToText("a <tag /> b"), is(equalTo("a b")));
		assertThat(Parse.htmlToText("a &nbsp;"), is(equalTo("a")));
		assertThat(Parse.htmlToText("&amp;nbsp;"), is(equalTo("&nbsp;")));
		assertThat(Parse.htmlToText("1 &nbsp; &nbsp; 2"), is(equalTo("1     2")));
		assertThat(Parse.htmlToText("1 \u00a0\u00a0\u00a0\u00a02"), is(equalTo("1     2")));
		assertThat(Parse.htmlToText("  <tag />a"), is(equalTo("a")));
		assertThat(Parse.htmlToText("a<br />b"), is(equalTo("a\nb")));

		assertThat(Parse.htmlToText("<font size=+1>a</font>b"), is(equalTo("ab")));
		assertThat(Parse.htmlToText("a<font size=+1>b</font>"), is(equalTo("ab")));
		assertThat(Parse.htmlToText("a<b"), is(equalTo("a<b")));

		assertThat(Parse.htmlToText("a<br>b<br/>c<  br   /   >d"), is(equalTo("a\nb\nc\nd")));
		assertThat(Parse.htmlToText("a</p><p>b"), is(equalTo("a\nb")));
		assertThat(Parse.htmlToText("a< / p >   <   p  >b"), is(equalTo("a\nb")));
	}

	@Test
	public void testUnescape() {
		assertThat(Parse.unescape("a&lt;b"), is(equalTo("a<b")));
		assertThat(Parse.unescape("a&gt;b&nbsp;&amp;&nbsp;b>c &&"), is(equalTo("a>b & b>c &&")));
		assertThat(Parse.unescape("&amp;amp;&amp;amp;"), is(equalTo("&amp;&amp;")));
		assertThat(Parse.unescape("a&gt;b&nbsp;&amp;&nbsp;b>c &&"), is(equalTo("a>b & b>c &&")));
		assertThat(Parse.unescape("“”‘’"), is(equalTo("\"\"''")));
	}

	@Test
	public void testWhitespaceIsCondensed() {
		assertThat(Parse.condenseWhitespace(" a  b  "), is(equalTo("a b")));
		assertThat(Parse.condenseWhitespace(" a  \n\tb  "), is(equalTo("a b")));
		assertThat(Parse.condenseWhitespace(" "), is(equalTo("")));
		assertThat(Parse.condenseWhitespace("  "), is(equalTo("")));
		assertThat(Parse.condenseWhitespace("   "), is(equalTo("")));
		assertThat(Parse.condenseWhitespace(new String(new char[]{(char) 160})), is(equalTo("")));
	}
}
