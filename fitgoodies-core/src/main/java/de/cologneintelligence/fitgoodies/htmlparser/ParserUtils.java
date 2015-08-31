package de.cologneintelligence.fitgoodies.htmlparser;

import org.jsoup.nodes.Element;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ParserUtils {
	private ParserUtils() {
	}

	public static boolean isIgnored(Element table) {
		final String ignore = table.attr(Constants.ATTR_IGNORE);
		return ignore != null && ignore.equalsIgnoreCase("true");
	}

	public static String getHtmlStackTrace(Throwable t) {
		StringWriter writer = new StringWriter();

		t.printStackTrace(new PrintWriter(writer));

		final String trace = writer.toString();
		return "<pre>" + toHtml(trace) + "</pre>";
	}

	private static String toHtml(String trace) {
		return trace
				.replaceAll("&", "&quot;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;")
				.replaceAll("\t", "&nbsp; &nbsp; ");
	}

}
