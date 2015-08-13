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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FitRowTest {

	@Mock
	FitTable table;

	@Mock
	Counts counts;

	private FitRow aRow(int row, String html) {
		Element table = Jsoup.parseBodyFragment(html).select("tr").first();
		return new FitRow(this.table, row, table, counts);
	}

	@Test
	public void argumentsAreSaved() {
		Element element = Jsoup.parseBodyFragment("<table><tr></tr></table>").select("tr").first();
		FitRow row = new FitRow(table, 0, element, counts);
		assertThat(row.getTable(), is(sameInstance(table)));
		assertThat(row.getRow(), is(sameInstance(element)));
	}

	@Test
	public void cellsAreCreated() {
		FitRow row1 = aRow(0, "<table><tr><td>1</td><td>2</td></tr></table>");
		FitRow row2 = aRow(0, "<table><tr><td>1</td><td>2</td><td>3</td></tr></table>");

		assertThat(row1.cells().size(), is(2));
		assertThat(row2.cells().size(), is(3));
		assertThat(row2.cells().get(2).getRow(), is(sameInstance(row2)));
		assertThat(row2.cells().get(2).getTd().text(), is(equalTo("3")));
	}

	@Test
	public void errorsAreForwarded() {
		FitRow row0 = aRow(0, "<table><tr><td>1</td><td>2</td></tr></table>");
		FitRow row1 = aRow(1, "<table><tr><td>1</td><td>2</td></tr></table>");
		FitRow row2 = aRow(2, "<table><tr><td>1</td><td>2</td></tr></table>");

		row0.exception("e1");
		row1.fail("e2");
		row2.fail("e6");

		verify(table).exceptionRow(0, "e1");
		verify(table).wrongRow(1, "e2");
		verify(table).wrongRow(2, "e6");
	}
}
