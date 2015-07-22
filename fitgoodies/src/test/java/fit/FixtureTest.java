package fit;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class FixtureTest {

	@Test
	public void testEscape() {
		assertThat(FitUtils.escape("     "), is(equalTo(" &nbsp; &nbsp; ")));

		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertThat(FitUtils.escape(junk), is(CoreMatchers.equalTo(junk)));
		assertThat(FitUtils.escape(""), is(CoreMatchers.equalTo("")));
		assertThat(FitUtils.escape("<"), is(CoreMatchers.equalTo("&lt;")));
		assertThat(FitUtils.escape("<<"), is(CoreMatchers.equalTo("&lt;&lt;")));
		assertThat(FitUtils.escape("x<"), is(CoreMatchers.equalTo("x&lt;")));
		assertThat(FitUtils.escape("&"), is(CoreMatchers.equalTo("&amp;")));
		assertThat(FitUtils.escape("<&<"), is(CoreMatchers.equalTo("&lt;&amp;&lt;")));
		assertThat(FitUtils.escape("&<&"), is(CoreMatchers.equalTo("&amp;&lt;&amp;")));
		assertThat(FitUtils.escape("a < b && c < d"), is(CoreMatchers.equalTo("a &lt; b &amp;&amp; c &lt; d")));
		assertThat(FitUtils.escape("a\nb"), is(CoreMatchers.equalTo("a<br />b")));
	}
}
