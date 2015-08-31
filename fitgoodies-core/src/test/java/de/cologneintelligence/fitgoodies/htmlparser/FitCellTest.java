package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FitCellTest {

	@Mock
	FitRow row;

	@Mock
	Counts counts;

	private FitCell aCell(String html) {
		Element table = Jsoup.parseBodyFragment("<table><tr>" + html + "</tr></table>").select("td").first();
		return new FitCell(row, table);
	}

	private FitCell aCell() {
		return aCell("<td>v</td>");
	}

	@Test
	public void argumentsAreSaved() {
		Element element = Jsoup.parseBodyFragment("<table><tr></tr></table>").select("tr").first();
		FitCell row = new FitCell(this.row, element);
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
		cell.ignore();

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

	@Test
	public void exceptionAsStringIsProcessed() {
		FitCell cell = aCell();
		cell.exception("my message");

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
		assertContent(cell, "<span class=\"fit-expected\">text</span> <span class=\"fit-info\">(expected)</span><br> " +
            "<span class=\"fit-actual\">other</span> <span class=\"fit-info\">(actual)</span>");
	}

	@Test
	public void cellTextIsModifiedWithException() {
		FitCell cell = aCell("<td>text</td>");
		cell.exception(anException());
		cell.finishExecution(counts);
		String content = cell.getTd().html();

		assertThat(content, containsString("<span class=\"fit-expected\">text</span> <span class=\"fit-info\">" +
            "(expected)</span><br> <span class=\"fit-actual-exception\">"));
		assertThat(content, containsString("testException"));
	}

	@Test
	public void cellTextIsModifiedWithStringException() {
		FitCell cell = aCell("<td>text</td>");
		cell.exception("an exception");
		cell.finishExecution(counts);
		String content = cell.getTd().html();

		assertThat(content, containsString("<span class=\"fit-expected\">text</span> <span class=\"fit-info\">" +
            "(expected)</span><br> <span class=\"fit-actual-exception\">"));
		assertThat(content, containsString("an exception"));
	}

    @Test
    public void internalValueCanBeSet() {
        FitCell cell = aCell("<td>text</td>");
        cell.setFitValue("new Value");
        assertThat(cell.getTd().outerHtml(), is(equalTo("<td>new Value</td>")));

        cell = aCell("<td data-fit-value=\"old-val\">text</td>");
        cell.setFitValue("new-val");
        assertThat(cell.getTd().outerHtml(), is(equalTo("<td data-fit-value=\"new-val\">text</td>")));
    }

	@Test
	public void cellTextIsModifiedWithInfo() {
		FitCell cell = aCell("<td>text</td>");
		cell.wrong("other", "more info");
		assertContent(cell, "<span class=\"fit-expected\">text</span> <span class=\"fit-info\">(expected)</span><br> " +
            "<span class=\"fit-actual\">other</span> <span class=\"fit-info\">(actual)</span><br>" +
            "<span class=\"fit-info\">[more info]</span>");
	}

    @Test
    public void canChangeDisplayText() {
        FitCell cell = aCell("<td>text</td>");
        cell.setDisplayValue("value");
        assertThat(cell.getTd().outerHtml(), is(equalTo("<td>value</td>")));

        cell = aCell("<td data-fit-value=\"test\">new text</td>");
        cell.setDisplayValue("another>value");
        assertThat(cell.getTd().outerHtml(), is(equalTo("<td data-fit-value=\"test\">another&gt;value</td>")));
    }

    @Test
    public void canChangeDisplayTextRaw() {
        FitCell cell = aCell("<td data-fit-value=\"test\">new text</td>");
        cell.setDisplayValueRaw("another&amp;<br>value");
        assertThat(cell.getTd().outerHtml(), is(equalTo("<td data-fit-value=\"test\">another&amp;<br>value</td>")));
    }

	@Test
	public void rightCellTextIsModified() {
		FitCell cell = aCell("<td>text</td>");
		cell.right("ok");
		assertContent(cell, "<span class=\"fit-expected\">text</span> <span class=\"fit-info\">(expected)</span><br> " +
            "<span class=\"fit-actual\">ok</span> <span class=\"fit-info\">(actual)</span>");
	}

    @Test
    public void canAppendText() {
        FitCell cell = aCell("<td>original</td>");
        cell.addDisplayValue(" text<br>");
        assertContent(cell, "original text&lt;br&gt;");
    }

    @Test
    public void canAppendRawText() {
        FitCell cell = aCell("<td>original</td>");
        cell.addDisplayValueRaw(" text<br>");
        assertContent(cell, "original text<br>");
    }

    @Test
    public void canCreateBlankCell() {
        FitCell cell = aCell("<td>original</td>");
        cell.blank("info", 3);
        cell.finishExecution(counts);
        assertThat(cell.getTd().attr("colspan"), is(equalTo("3")));
        assertThat(cell.getTd().html(), containsString("<span class=\"fit-info\">[info]</span>"));
    }

    @Test
    public void rawInfoIsAppended() {
        FitCell cell = aCell("<td>original</td>");
        cell.rawInfo("<br>");
        cell.rawInfo("a message");
        cell.finishExecution(counts);

        assertThat(cell.getTd().html(), containsString("original"));
        assertThat(cell.getTd().html(), containsString("[<br> a message]"));
    }

	private void assertContent(FitCell cell, String expected) {
		cell.finishExecution(counts);
		assertThat(cell.getTd().html(), is(equalTo(expected)));
	}

	private void assertClass(FitCell cell, State expectedState) {
		cell.finishExecution(counts);

		for (State state : State.values()) {
			if (state.cssClass != null) {
				assertThat(cell.getTd().hasClass(state.cssClass), is(state == expectedState));
			}
		}
	}
}
