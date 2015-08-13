package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FitRow {
	private final FitTable table;
	private final int index;
	private final Element row;
	private final Counts counts;

	private List<FitCell> cells;

	public FitRow(FitTable fitTable, int index, Element row, Counts counts) {
		this.table = fitTable;
		this.row = row;
		this.index = index;
		this.counts = counts;

		parseRow();
	}

	private void parseRow() {
		List<FitCell> cells = new LinkedList<>();
		for (Element td : row.select("td")) {
			cells.add(new FitCell(this, td, counts));
		}
		this.cells = Collections.unmodifiableList(cells);
	}

	public Element getRow() {
		return row;
	}

	public FitTable getTable() {
		return table;
	}

	public List<FitCell> cells() {
		return cells;
	}

	public void exception(String text) {
		table.exceptionRow(index, text);
	}

	public void fail(String text) {
		table.wrongRow(index, text);
	}
}
