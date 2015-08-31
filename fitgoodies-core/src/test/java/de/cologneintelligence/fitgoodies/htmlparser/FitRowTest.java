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
        FitRow fitRow = new FitRow(this.table, table);
        fitRow.updateIndex(row);
        return fitRow;
	}

	@Test
	public void argumentsAreSaved() {
		Element element = Jsoup.parseBodyFragment("<table><tr></tr></table>").select("tr").first();
		FitRow row = new FitRow(table, element);
        row.updateIndex(0);
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
		row1.wrong("e2");
		row2.wrong("e6");

		verify(table).exceptionRow(0, "e1");
		verify(table).wrongRow(1, "e2");
		verify(table).wrongRow(2, "e6");
	}

    @Test
    public void testInsert() {
        FitRow row = aRow(0, "<table><tr><td>1</td><td>2</td></tr></table>");
        FitCell td1 = row.insert(0);
        FitCell td2 = row.insert(2);

        assertThat(row.cells().get(0), is(sameInstance(td1)));
        assertThat(row.cells().get(1).getTd().html(), is("1"));
        assertThat(row.cells().get(2), is(sameInstance(td2)));
        assertThat(row.cells().get(3).getTd().html(), is("2"));
        assertThat(row.getRow().select("td").size(), is(4));
    }

    @Test
    public void testRemove() {
        FitRow row = aRow(0, "<table><tr><td>1</td><td>2</td><td>3</td><td>4</td></tr></table>");
        row.remove(1);
        row.remove(2);

        assertThat(row.cells().get(0).getTd().html(), is("1"));
        assertThat(row.cells().get(1).getTd().html(), is("3"));

        assertThat(row.getRow().select("td").size(), is(2));
        assertThat(row.getRow().select("td").get(1).html(), is("3"));
    }
}
