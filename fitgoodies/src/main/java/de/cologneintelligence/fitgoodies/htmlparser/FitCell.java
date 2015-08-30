package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class FitCell {
    private final FitRow row;
    private final Element td;

    private State state = State.NONE;
    private String actual;
    private String info;

    public FitCell(FitRow fitRow, Element td) {
        this.row = fitRow;
        this.td = td;
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

    public void finishExecution(Counts counts) {
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
        }

        if (actual != null || info != null) {
            String html = "<span class=\"fit-expected\">" + td.html() + "</span>";

            if (actual != null) {
                html += " <span class=\"fit-info\">(expected)</span><br>";

                if (state != State.EXCEPTION) {
                    html += " <span class=\"fit-actual\">" + actual + "</span>";
                    html += " <span class=\"fit-info\">(actual)</span>";
                } else {
                    html += " <span class=\"fit-actual-exception\">" + actual + "</span>";
                    html += " <span class=\"fit-info\">(exception)</span>";
                }
            }

            if (info != null) {
                html += "<br><span class=\"fit-info\">[" + info + "]</span>";
            }

            td.html(html);
        }
    }

    public void right() {
        right(null, null);
    }

    public void right(String actual) {
        right(actual, null);
    }

    public void right(String actual, String info) {
        this.state = State.RIGHT;
        this.actual = actual;
        info(info);
    }

    public void exception(Throwable t) {
        this.state = State.EXCEPTION;
        this.actual = ParserUtils.getHtmlStackTrace(t);
    }

    public void exception(String message) {
        this.state = State.EXCEPTION;
        this.actual = message;
    }

    public void ignore() {
        ignore(null);
    }

    public void ignore(String info) {
        this.state = State.IGNORED;
        this.actual = null;
        info(info);
    }

    public void info(String message) {
        if (message == null) {
            return;
        }

        rawInfo(new TextNode(message, td.baseUri()).outerHtml());
    }

    public void wrong() {
        wrong(null, null);
    }

    public void wrong(String actual) {
        wrong(actual, null);
    }

    public void wrong(String actual, String info) {
        this.state = State.WRONG;
        this.actual = actual;
        info(info);
    }

    public void setFitValue(String value) {
        if (td.hasAttr("data-fit-value")) {
            td.attr(Constants.ATTR_VALUE, value);
        } else {
            setDisplayValue(value);
        }
    }

    public void setDisplayValue(String s) {
        td.text(s);
    }

    public void setDisplayValueRaw(String s) {
        td.html(s);
    }

    public void addDisplayValue(String text) {
        setDisplayValue(td.text() + text);
    }

    public void addDisplayValueRaw(String s) {
        td.html(td.html() + s);
    }

    public void blank(String info, int length) {
        td.attr("colspan", Integer.toString(length));
        info(info);
    }

    public void rawInfo(String html) {
        if (this.info == null) {
            this.info = "";
        } else {
            this.info += " ";
        }

        this.info += html;
    }

    @Override
    public String toString() {
        return "[Cell: " + td.outerHtml() + ", at " + row.getIndex() + "/" + td.siblingIndex() + "]";
    }
}
