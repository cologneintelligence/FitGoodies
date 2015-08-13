package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FitCellTest {

	@Mock
	FitRow row;

	@Mock
	Counts counts;

	private FitCell aCell(String html) {
		Element table = Jsoup.parseBodyFragment("<table><tr>" + html + "</tr></table>").select("td").first();
		return new FitCell(row, table, counts);
	}

	private FitCell aCell() {
		return aCell("<td>v</td>");
	}

	@Test
	public void argumentsAreSaved() {
		Element element = Jsoup.parseBodyFragment("<table><tr></tr></table>").select("tr").first();
		FitCell row = new FitCell(this.row, element, counts);
		assertThat(row.getRow(), is(sameInstance(this.row)));
		assertThat(row.getTd(), is(sameInstance(element)));
	}

	@Test
	public void valueCanBeReceived() {
		String value1 = aCell("<td>value1</td>").getFitValue();
		String value2 = aCell("<td>value2</td>").getFitValue();

		assertThat(value1, is(equalTo("value1")));
		assertThat(value2, is(equalTo("value2")));
	}

	@Test
	public void fitValueCanBeReceived() {
		String value1 = aCell("<td data-fit-value=\"result()\">value1</td>").getFitValue();
		String value2 = aCell("<td data-fit-value=\"test\">value2</td>").getFitValue();

		assertThat(value1, is(equalTo("result()")));
		assertThat(value2, is(equalTo("test")));
	}

	@Test
	public void wrongIsProcessed() {
		FitCell cell = aCell();
		cell.wrong();

		assertClass(cell, State.WRONG);
		assertThat(counts.wrong, is(1));
	}

	@Test
	public void rightIsProcessed() {
		FitCell cell = aCell();
		cell.wrong();
		cell.right();

		assertClass(cell, State.RIGHT);
		assertThat(counts.right, is(1));
	}

	@Test
	public void ignoredIsProcessed() {
		FitCell cell = aCell();
		cell.wrong();
		cell.ignored();

		assertClass(cell, State.IGNORED);
		assertThat(counts.ignores, is(1));
	}

	@Test
	public void exceptionIsProcessed() {
		FitCell cell = aCell();
		cell.exception(anException());

		assertClass(cell, State.EXCEPTION);
		assertThat(counts.exceptions, is(1));
	}

	private Throwable anException() {
		Exception e = new Exception("testException");
		e.fillInStackTrace();
		return e;
	}

	@Test
	public void cellTextIsKept() {
		FitCell cell = aCell("<td>text</td>");
		cell.wrong();
		assertContent(cell, "text");
	}

	@Test
	public void cellTextIsModified() {
		FitCell cell = aCell("<td>text</td>");
		cell.wrong("other");
		assertContent(cell, "<span class=\"fit-actual\">text</span> <span class=\"fit-info\">(actual)</span><br> " +
				"<span class=\"fit-expected\">other</span> <span class=\"fit-info\">(expected)</span>");
	}

	@Test
	public void cellTextIsModifiedWithException() {
		FitCell cell = aCell("<td>text</td>");
		cell.exception(anException());
		cell.finishExecution();
		String content = cell.getTd().html();

		assertThat(content, containsString("<span class=\"fit-actual\">text</span> <span class=\"fit-info\">" +
				"(actual)</span><br> <span class=\"fit-expected\">"));
		assertThat(content, containsString("testException"));
	}

	@Test
	public void cellTextIsModifiedWithInfo() {
		FitCell cell = aCell("<td>text</td>");
		cell.wrong("other", "more info");
		assertContent(cell, "<span class=\"fit-actual\">text</span> <span class=\"fit-info\">(actual)</span><br> " +
				"<span class=\"fit-expected\">other</span> <span class=\"fit-info\">(expected)</span><br>" +
				"<span class=\"fit-info\">[more info]</span>");
	}

	@Test
	public void rightCellTextIsModified() {
		FitCell cell = aCell("<td>text</td>");
		cell.right("ok");
		assertContent(cell, "<span class=\"fit-actual\">text</span> <span class=\"fit-info\">(actual)</span><br> " +
				"<span class=\"fit-expected\">ok</span> <span class=\"fit-info\">(expected)</span>");
	}

	private void assertContent(FitCell cell, String expected) {
		cell.finishExecution();
		assertThat(cell.getTd().html(), is(equalTo(expected)));
	}

	private void assertClass(FitCell cell, State expectedState) {
		cell.finishExecution();

		for (State state : State.values()) {
			if (state.cssClass != null) {
				assertThat(cell.getTd().hasClass(state.cssClass), is(state == expectedState));
			}
		}
	}
}
