package fit;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class FixtureTest {

	@Test
	public void testEscape() {
		assertThat(Fixture.escape("     "), is(equalTo(" &nbsp; &nbsp; ")));

		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertThat(Fixture.escape(junk), is(CoreMatchers.equalTo(junk)));
		assertThat(Fixture.escape(""), is(CoreMatchers.equalTo("")));
		assertThat(Fixture.escape("<"), is(CoreMatchers.equalTo("&lt;")));
		assertThat(Fixture.escape("<<"), is(CoreMatchers.equalTo("&lt;&lt;")));
		assertThat(Fixture.escape("x<"), is(CoreMatchers.equalTo("x&lt;")));
		assertThat(Fixture.escape("&"), is(CoreMatchers.equalTo("&amp;")));
		assertThat(Fixture.escape("<&<"), is(CoreMatchers.equalTo("&lt;&amp;&lt;")));
		assertThat(Fixture.escape("&<&"), is(CoreMatchers.equalTo("&amp;&lt;&amp;")));
		assertThat(Fixture.escape("a < b && c < d"), is(CoreMatchers.equalTo("a &lt; b &amp;&amp; c &lt; d")));
		assertThat(Fixture.escape("a\nb"), is(CoreMatchers.equalTo("a<br />b")));
	}
}
