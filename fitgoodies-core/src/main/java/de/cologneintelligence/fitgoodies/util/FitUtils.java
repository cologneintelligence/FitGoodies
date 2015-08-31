/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.util;/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FitUtils {

	private FitUtils() {
	}

	private static Pattern PARAMETER_PATTERN = Pattern.compile("^(.*)\\s*\\[\\s*(.*?)\\s*\\]\\s*$");
	private static Pattern MULTI_PARAMETER_PATTERN = Pattern.compile("^\\s*([^=]+?)\\s*=\\s*(.*?)\\s*$");

	public static String HTML_GREEN = "#cfffcf";
	public static String HTML_RED = "#ffcfcf";
	public static String HTML_GREY = "#efefef";
	public static String HTML_YELLOW = "#ffffcf";
	public static String HTML_INFO = "#808080";

	public static String label(String string) {
		return " <font size=-1 color=\"#c08080\"><i>" + string + "</i></font>";
	}

	public static String escape(String string) {
		string = string.replaceAll("&", "&amp;");
		string = string.replaceAll("<", "&lt;");
		string = string.replaceAll("  ", " &nbsp;");
		string = string.replaceAll("\r\n", "<br />");
		string = string.replaceAll("\r", "<br />");
		string = string.replaceAll("\n", "<br />");
		return string;
	}

	public static String camel(String name) {
		StringBuilder b = new StringBuilder(name.length());
		StringTokenizer t = new StringTokenizer(name);
		if (!t.hasMoreTokens())
			return name;
		b.append(t.nextToken());
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			b.append(token.substring(0, 1).toUpperCase());      // replace spaces with camelCase
			b.append(token.substring(1));
		}
		return b.toString();
	}

	public static String htmlSafeFile(File file) {
		return htmlSafeFile(file.getPath());
	}

	public static String htmlSafeFile(String file) {
		return file.replace(File.separatorChar, '/');
	}

	public static <T> T saveGet(int col, T[] array) {
		if (col < array.length) {
			return array[col];
		} else {
			return null;
		}
	}

	/**
	 * extracts and removes parameters from a cell.
	 *
	 * @param cell cell to process
	 * @return the extracted parameter or {@code null}
	 */
	public static String extractCellParameter(FitCell cell) {
		final Matcher matcher = PARAMETER_PATTERN.matcher(cell.getFitValue());
		if (matcher.matches()) {
			cell.setFitValue(matcher.group(1));
			return matcher.group(2);
		} else {
			return null;
		}
	}

	public static Map<String, String> extractCellParameterMap(FitCell cell) {
		String parameters = extractCellParameter(cell);
		if (parameters == null) {
			return Collections.emptyMap();
		}

		Map<String, String> result = new HashMap<>();
		for (String keyValue : parameters.split(",")) {
			Matcher matcher = MULTI_PARAMETER_PATTERN.matcher(keyValue);

			if (matcher.find()) {
				result.put(matcher.group(1), matcher.group(2));
			}
		}

		return result;
	}
}
