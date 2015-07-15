package fit;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileRunnerTest {
	Parse tempParse;

	String simpleHtml =
			"<table>" +
					"    <tr><td>fit.Fixture</td></tr>" +
					"</table>";

	String wikiHtml =
			"<table><tr><td>extra formatting" +
					"   <wiki>" +
					"       <table>" +
					"           <tr><td>fit.Fixture</td></tr>" +
					"       </table>" +
					"   </wiki>" +
					"</td></tr></table>";


	@Test
	public void testRunningFileWithWikiTag() {
		testHtml(simpleHtml);
		testHtml(wikiHtml);
	}

	private void testHtml(String html) {
		FileRunner runner = new FileRunner();
		runner.fixture = new Fixture() {
			public void doTables(Parse tables) {
				tempParse = tables;
			}
		};
		runner.input = html;
		runner.output = new PrintWriter(new StringWriter());
		runner.process();

		assertThat(tempParse.leaf().text(), is(equalTo("fit.Fixture")));
	}
}

