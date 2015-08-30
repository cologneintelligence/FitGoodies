package de.cologneintelligence.fitgoodies.htmlparser;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FitRow {
    private static final String TAG = "td";

	private final FitTable table;
	private int index;
	private final Element row;

    private List<FitCell> cells = new LinkedList<>();

    public FitRow(FitTable fitTable, Element row) {
		this.table = fitTable;
		this.row = row;

        for (Element td : row.select(TAG)) {
            cells.add(new FitCell(this, td));
        }
	}

	public Element getRow() {
		return row;
	}

	public FitTable getTable() {
		return table;
	}

	public List<FitCell> cells() {
		return Collections.unmodifiableList(cells);
	}

    public int size() {
        return cells.size();
    }

	public void exception(String text) {
		table.exceptionRow(index, text);
	}

    public void exception(Throwable t) {
        exception(ParserUtils.getHtmlStackTrace(t));
    }

	public void wrong(String text) {
		table.wrongRow(index, text);
	}

    public FitCell insert(int index) {
        Element td = new Element(Tag.valueOf(TAG), row.baseUri());
        row.insertChildren(index, Collections.singleton(td));

        FitCell fitCell = new FitCell(this, td);
        cells.add(index, fitCell);
        return fitCell;
    }

    public void remove(int index) {
        row.select(TAG).get(index).remove();
        cells.remove(index);
    }

    public FitCell append() {
        return insert(cells.size());
    }

    public int getIndex() {
        return index;
    }

    void updateIndex(int index) {
        this.index = index;
    }
}
