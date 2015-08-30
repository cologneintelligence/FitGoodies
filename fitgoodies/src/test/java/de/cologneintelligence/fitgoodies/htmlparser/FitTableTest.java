package de.cologneintelligence.fitgoodies.htmlparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class FitTableTest {
	private static final String EMPTY_HTML_TABLE = "<table class=\"oldclass\"><tr><td></td></tr></table>";

	private FitTable aTable(String html) {
		Element table = Jsoup.parseBodyFragment(html).select("table").first();
		return new FitTable(table);
	}

	@Test
	public void argumentsAreSaved() {
		Element element = Jsoup.parseBodyFragment(EMPTY_HTML_TABLE).select("table").first();
		FitTable table = new FitTable(element);
		assertThat(table.getTable(), is(sameInstance(element)));
	}

	@Test
	public void failuresAreReported() {
		FitTable table = aTable(EMPTY_HTML_TABLE);
		table.getCounts().wrong++;
		assertCssClasses(table, State.WRONG);
	}

	@Test
	public void exceptionsAreReported() {
		FitTable table = aTable(EMPTY_HTML_TABLE);
		table.getCounts().wrong++;
		table.getCounts().exceptions++;
		assertCssClasses(table, State.EXCEPTION);
	}

	@Test
	public void successIsReported() {
		FitTable table = aTable(EMPTY_HTML_TABLE);
		assertCssClasses(table, State.RIGHT);
	}

	@Test
	public void oldStyleFixtureClassIsFound() {
		FitTable table1 = aTable("<table><tr><td>myClass1</td></tr><tr><td>1</td></tr></table>");
		FitTable table2 = aTable("<table><tr><td>myClass2</td></tr><tr><td>1</td></tr><tr><td>2</td></tr></table>");

		assertThat(table1.getFixtureClass(), is(equalTo("myClass1")));
		assertThat(table2.getFixtureClass(), is(equalTo("myClass2")));
		assertThat(table1.rows().size(), is(1));
		assertThat(table2.rows().size(), is(2));
	}

	@Test
	public void newStyleFixtureClassIsFound() {
		FitTable table1 = aTable("<table data-fit-fixture=\"myClass1\"><tr><td>0</td></tr><tr><td>1</td></tr></table>");
		FitTable table2 = aTable("<table data-fit-fixture=\"myClass2\"><tr><td>0</td></tr><tr><td>1</td></tr><tr><td>2</td></tr></table>");

		assertThat(table1.getFixtureClass(), is(equalTo("myClass1")));
		assertThat(table2.getFixtureClass(), is(equalTo("myClass2")));
		assertThat(table1.rows().size(), is(2));
		assertThat(table2.rows().size(), is(3));
	}

	@Test
	public void nonIgnoredRowsAreForwarded() {
		FitTable table1 = aTable("<table data-fit-fixture=\"myClass1\"><tr><td>0</td></tr><tr data-fit-ignore=\"true\"><td>1</td></tr></table>");
		FitTable table2 = aTable("<table data-fit-fixture=\"myClass2\"><tr data-fit-ignore=\"true\"><td>2</td></tr><tr><td>3</td></tr><tr><td>4</td></tr></table>");

		assertThat(table1.rows().size(), is(1));
		assertThat(table1.rows().get(0).getRow().text(), is(equalTo("0")));
		assertThat(table2.rows().size(), is(2));
		assertThat(table2.rows().get(0).getRow().text(), is(equalTo("3")));
		assertThat(table2.rows().get(1).getRow().text(), is(equalTo("4")));
	}

	@Test
	public void emptyTableLogsException() {
		Element element = Jsoup.parseBodyFragment("<table></table>").select("table").first();

		try {
			new FitTable(element);
			Assert.fail("Expected exception from constructor");
		} catch (IllegalArgumentException ignore) {
		}

		assertThat(element.select("td").size(), is(1));
		assertCssClasses(element, State.EXCEPTION);
	}

	@Test
	public void classicTableDisplaysLastError() {
		Exception exception1 = new Exception("test1");
		exception1.fillInStackTrace();
		Exception exception2 = new Exception("test2");
		exception2.fillInStackTrace();

		FitTable table = aTable(EMPTY_HTML_TABLE);
		table.exception(exception1);
		table.exception(exception2);

		Elements tds = table.getTable().select("tr").first().select("td");
		assertThat(tds.size(), is(2));
		assertThat(tds.get(1).text().isEmpty(), is(true));
		assertThat(tds.get(0).html(), containsString(ParserUtils.getHtmlStackTrace(exception2)));
		assertCssClasses(table, State.EXCEPTION);
		assertCounts(table, 0, 0, 2, 0);
	}

	@Test
	public void newStyleTableDisplaysLastError() {
		Exception exception1 = new Exception("test1");
		exception1.fillInStackTrace();

		FitTable table = aTable("<table class=\"oldclass\" data-fit-fixture=\"test\"><tr><td>content</td></tr></table>");
		table.exception(exception1);

		Elements trs = table.getTable().select("tr");
		assertThat(trs.size(), is(2));

		assertThat(trs.get(0).select("td").first().html(), containsString(ParserUtils.getHtmlStackTrace(exception1)));

		Elements tds = trs.get(1).select("td");
		assertThat(tds.get(0).text().isEmpty(), is(true));
		assertThat(tds.get(1).text(), is(equalTo("content")));

		assertCssClasses(table, State.EXCEPTION);
		assertCounts(table, 0, 0, 1, 0);

	}

	@Test
	public void newStyleArgumentsAreExported() {
		FitTable table1 = aTable("<table data-fit-fixture=\"\" data-fit-arg-FILE=\"file2\" data-fit-arg-dir=\"dir\"><tr><td>0</td></tr></table>");
		FitTable table2 = aTable("<table data-fit-fixture=\"\" data-fit-arg-test=\"value\"><tr><td>0</td></tr></table>");

		Map<String, String> args1 = new HashMap<>();
		args1.put("file", "file2");
		args1.put("dir", "dir");

		Map<String, String> args2 = new HashMap<>();
		args2.put("test", "value");

		assertThat(table1.getArguments(), is(equalTo(args1)));
		assertThat(table2.getArguments(), is(equalTo(args2)));
	}

	@Test
	public void oldStyleArgumentsAreExported() {
		FitTable table1 = aTable("<table><tr><td></td><td>file=file2</td><td>DIR = dir</td><td>three</td></tr></table>");
		FitTable table2 = aTable("<table><tr><td></td><td>   test    = value   </td></tr></table>");

		Map<String, String> args1 = new HashMap<>();
		args1.put("file", "file2");
		args1.put("0", "file2");
		args1.put("dir", "dir");
		args1.put("1", "dir");
		args1.put("2", "three");

		Map<String, String> args2 = new HashMap<>();
		args2.put("test", "value");
		args2.put("0", "value");

		assertThat(table1.getArguments(), is(equalTo(args1)));
		assertThat(table2.getArguments(), is(equalTo(args2)));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void tablesAreUnmodifiable() throws IOException {
		aTable(EMPTY_HTML_TABLE).rows().add(null);
	}

	@Test
	public void rowsCanStoreErrors() {
		FitTable table = aTable("<table data-fit-fixture=\"test\"><tr><td>1</td></tr><tr><td>2</td></tr><tr><td>3</td></tr></table>");
		table.wrongRow(0, "message 1");
		table.wrongRow(0, "message 2");
		table.wrongRow(2, "message 3");
		table.finishExecution();

		final Elements trs = table.getTable().select("tr");
		assertThat(trs.get(0).select("td").first().text(), is(equalTo("message 2")));
		assertThat(trs.get(0).select("td").hasClass(Constants.CSS_WRONG), is(true));
		assertThat(trs.get(1).select("td").hasClass(Constants.CSS_WRONG), is(false));
		assertThat(trs.get(2).select("td").first().text(), is(equalTo("message 3")));
		assertThat(trs.get(2).select("td").hasClass(Constants.CSS_WRONG), is(true));

		assertCssClasses(table.getTable(), State.WRONG);
		assertCounts(table, 0, 2, 0, 0);
	}

	@Test
	public void rowsCanStoreExceptions() {
		FitTable table = aTable("<table data-fit-fixture=\"test\"><tr><td>1</td></tr><tr><td>2</td></tr><tr><td>3</td></tr></table>");
		table.wrongRow(0, "message 1");
		table.exceptionRow(0, "message 2");
		table.wrongRow(0, "message 3");
		table.wrongRow(2, "message 4");
		table.finishExecution();

		final Elements trs = table.getTable().select("tr");
		assertThat(trs.get(0).select("td").first().text(), is(equalTo("message 2")));
		assertThat(trs.get(0).select("td").hasClass(Constants.CSS_EXCEPTION), is(true));

		assertCssClasses(table.getTable(), State.EXCEPTION);
		assertCounts(table, 0, 1, 1, 0);
	}

	private void assertCssClasses(FitTable table, State state) {
		table.finishExecution();
		assertThat(table.getTable().hasClass("oldclass"), is(true));
		assertCssClasses(table.getTable(), state);
	}

	private void assertCssClasses(Element table, State expectedState) {
		for (State state : State.values()) {
			if (state.cssClass != null) {
				assertThat(table.hasClass(state.cssClass), is(state == expectedState));
			}
		}
	}

	private void assertCounts(FitTable table, int right, int wrong, int exceptions, int ignored) {
		assertThat(table.getCounts().right, is(right));
		assertThat(table.getCounts().wrong, is(wrong));
		assertThat(table.getCounts().exceptions, is(exceptions));
		assertThat(table.getCounts().ignores, is(ignored));
	}
}
