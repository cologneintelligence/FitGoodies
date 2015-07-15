package fit;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.io.PrintWriter;

public class FileRunnerTest extends TestCase {
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

        assertEquals("fit.Fixture", tempParse.leaf().text());
    }
}

