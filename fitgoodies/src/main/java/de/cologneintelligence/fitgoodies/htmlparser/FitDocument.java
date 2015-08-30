package de.cologneintelligence.fitgoodies.htmlparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class FitDocument {
	private Document document;
	private List<FitTable> tables;

	public static FitDocument parse(InputStream is, String charset) throws IOException {
		final Document document = Jsoup.parse(is, charset, ".");
		return new FitDocument(document);
	}

	private FitDocument(Document document) {
		this.document = document;
		this.tables = Collections.unmodifiableList(findTables());
	}

	private List<FitTable> findTables() {
		final LinkedList<FitTable> result = new LinkedList<>();
		for (Element table : document.select("table")) {
			if (!ParserUtils.isIgnored(table)) {
				try {
					result.add(new FitTable(table));
				} catch (IllegalArgumentException ignored) {
				}
			}
		}
		return result;
	}

	public String getHtml() {
		return document.html();
	}

	public List<FitTable> tables() {
		return tables;
	}
}
