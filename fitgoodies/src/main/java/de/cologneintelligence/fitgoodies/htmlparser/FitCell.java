package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.nodes.Element;

public class FitCell {
	private final FitRow row;
	private final Element td;
	private final Counts counts;

	private State state = State.NONE;
	private String expected;
	private String info;

	public FitCell(FitRow fitRow, Element td, Counts counts) {
		this.row = fitRow;
		this.td = td;
		this.counts = counts;
	}

	public FitRow getRow() {
		return row;
	}

	public Element getTd() {
		return td;
	}

	public String getFitValue() {
		if (td.hasAttr("data-fit-value")) {
			return td.attr(Constants.ATTR_VALUE);
		} else {
			return td.html();
		}
	}

	public void finishExecution() {
		if (state != State.NONE) {
			td.addClass(state.cssClass);
			switch (state) {
				case WRONG:
					counts.wrong++;
					break;
				case RIGHT:
					counts.right++;
					break;
				case EXCEPTION:
					counts.exceptions++;
					break;
				case IGNORED:
					counts.ignores++;
					break;
			}

			if (expected != null || info != null) {
				String html = "<span class=\"fit-actual\">" + td.html() + "</span>";

				if (expected != null) {
					html += " <span class=\"fit-info\">(actual)</span><br>"
							+ " <span class=\"fit-expected\">" + expected + "</span>"
							+ " <span class=\"fit-info\">(expected)</span>";
				}

				if (info != null) {
					html += "<br><span class=\"fit-info\">[" + info + "]</span>";
				}

				td.html(html);
			}
		}
	}

	public void right() {
		right(null, null);
	}

	public void right(String expected) {
		right(expected, null);
	}

	public void right(String expected, String info) {
		this.state = State.RIGHT;
		this.expected = expected;
		this.info = info;
	}

	public void exception(Throwable t) {
		this.state = State.EXCEPTION;
		this.expected = ParserUtils.getHtmlStackTrace(t);
		this.info = null;
	}

	public void ignored() {
		ignored(null);
	}

	public void ignored(String info) {
		this.state = State.IGNORED;
		this.expected = null;
		this.info = info;
	}

	public void wrong() {
		wrong(null, null);
	}

	public void wrong(String expected) {
		wrong(expected, null);
	}

	public void wrong(String expected, String info) {
		this.state = State.WRONG;
		this.expected = expected;
		this.info = info;
	}
}
