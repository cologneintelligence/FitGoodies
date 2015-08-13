package de.cologneintelligence.fitgoodies.htmlparser;

import de.cologneintelligence.fitgoodies.Counts;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FitTable {

	public static final Pattern ARGUMENT_PATTERN = Pattern.compile("^\\s*([^=]+?)\\s*=\\s*(.*?)\\s*$");

	private final Element table;
	private boolean newStyleTable;
	private final Counts counts = new Counts();

	private boolean hasFeedbackColumn;
	private Map<Integer, State> errorColumnStates = new HashMap<>();
	private Map<Integer, String> errorColumnMessages = new HashMap<>();

	private String fixtureClass;
	private Map<String, String> arguments;
	private List<FitRow> rows = new LinkedList<>();

	public FitTable(Element table) {
		this.table = table;

		if (!checkTable()) {
			finishExecution();
			throw new IllegalArgumentException("Table is not valid");
		}

		parseTable();
	}

	private void parseTable() {
		int skip;
		Map<String, String> args = new HashMap<>();

		Elements trs = table.select("tr");
		newStyleTable = table.hasAttr(Constants.ATTR_FIXTURE);
		if (newStyleTable) {
			parseNewStyleHeader(args);
			skip = 0;
		} else {
			parseOldStyleHeader(trs.first(), args);
			skip = 1;
		}

		arguments = Collections.unmodifiableMap(args);

		for (Element tr : trs.subList(skip, trs.size())) {
			if (!ParserUtils.isIgnored(tr)) {
				rows.add(new FitRow(this, rows.size(), tr, counts));
			}
		}
	}

	private boolean checkTable() {
		Elements trs = table.select("tr");
		if (trs.size() == 0 || trs.select("td").size() == 0) {
			exceptionTable("Incomplete table definition");
			return false;
		}

		return true;
	}

	private void parseOldStyleHeader(Element tr, Map<String, String> args) {
		final Elements tds = tr.select("td");
		fixtureClass = tds.first().text();

		List<Element> subList = tds.subList(1, tds.size());
		for (int i = 0; i < subList.size(); i++) {
			Element element = subList.get(i);

			Matcher matcher = ARGUMENT_PATTERN.matcher(element.text());
			if (matcher.find()) {
				args.put(matcher.group(1).toLowerCase(), matcher.group(2));
				args.put(Integer.toString(i), matcher.group(2));
			} else {
				args.put(Integer.toString(i), element.text());
			}
		}
	}

	private void parseNewStyleHeader(Map<String, String> args) {
		fixtureClass = table.attr(Constants.ATTR_FIXTURE);

		for (Attribute attribute : table.attributes()) {
			final String attributeName = attribute.getKey().toLowerCase();
			if (attributeName.startsWith(Constants.ATTR_ARGUMENT_PREFIX)) {
				args.put(attributeName.substring(Constants.ATTR_ARGUMENT_PREFIX.length()),
						attribute.getValue());
			}
		}
	}

	public Element getTable() {
		return table;
	}

	public List<FitRow> rows() {
		return Collections.unmodifiableList(rows);
	}

	public String getFixtureClass() {
		return fixtureClass;
	}

	public Map<String, String> getArguments() {
		return arguments;
	}

	public Counts getCounts() {
		return counts;
	}

	public void finishExecution() {
		processRowErrors();

		for (FitRow row : rows) {
			for (FitCell cell : row.cells()) {
				cell.finishExecution();
			}
		}

		if (counts.exceptions > 0) {
			table.addClass(Constants.CSS_EXCEPTION);
		} else if (counts.wrong > 0) {
			table.addClass(Constants.CSS_WRONG);
		} else {
			table.addClass(Constants.CSS_RIGHT);
		}
	}

	private void processRowErrors() {
		if (!errorColumnStates.isEmpty()) {
			addFeedbackColumn(false);
			for (Map.Entry<Integer, String> entry : errorColumnMessages.entrySet()) {
				Element td = rows.get(entry.getKey()).getRow().select("td").first();
				State state = errorColumnStates.get(entry.getKey());

				td.text(entry.getValue());
				td.addClass(state.cssClass);

				if (state == State.EXCEPTION) {
					counts.exceptions++;
				} else {
					counts.wrong++;
				}
			}
		}
	}

	public void exceptionTable(Throwable t) {
		exceptionTable(ParserUtils.getHtmlStackTrace(t));
	}

	private void exceptionTable(String html) {
		addFeedbackColumn(true);
		counts.exceptions++;

		Element firstTd = table.select("tr").first().select("td").first();
		firstTd
				.html(html)
				.addClass(Constants.CSS_EXCEPTION);
	}

	private void addFeedbackColumn(boolean newRow) {
		if (hasFeedbackColumn) {
			return;
		}

		hasFeedbackColumn = true;

		if (table.select("tr").size() == 0 || (newStyleTable && newRow)) {
			table.prependElement("tr");
		}

		for (Element tr : table.select("tr")) {
			tr.prependElement("td")
					.addClass(Constants.CSS_FEEDBACK_COLUMN);
		}
	}

	void exceptionRow(int row, String text) {
		errorColumnMessages.put(row, text);
		errorColumnStates.put(row, State.EXCEPTION);
	}

	void wrongRow(int row, String text) {
		if (!errorColumnStates.containsKey(row) || !errorColumnStates.get(row).equals(State.EXCEPTION)) {
			errorColumnMessages.put(row, text);
			errorColumnStates.put(row, State.WRONG);
		}
	}
}
