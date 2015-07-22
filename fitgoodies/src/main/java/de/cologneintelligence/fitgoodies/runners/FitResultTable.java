/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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


package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Counts;
import fit.FitUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static de.cologneintelligence.fitgoodies.util.FixtureTools.htmlSafeFile;

/**
 * Implementation of FitResult which generates a indented HTML table.
 *
 */
public final class FitResultTable implements FitResult {
	private final List<FileCount> results = new LinkedList<>();
	private final FileSystemDirectoryHelper dirHelper;

	/**
	 * Generates a new Object.
	 * @param helper helper object to convert pathnames
	 */
	public FitResultTable(final FileSystemDirectoryHelper helper) {
		dirHelper = helper;
	}

	/**
	 * Saves the result <code>result</code> of the file {@code file}.
	 * @param file filename to identify the result
	 * @param result results
	 */
        @Override
	public void put(final File file, final Counts result) {
		FileCount fileCount = new FileCount(file, result);

		if (results.contains(fileCount)) {
			results.remove(fileCount);
		}

		results.add(fileCount);
	}

	/**
	 * Returns the {@code Counts} of a filename.
	 * @param file filename to look up
	 * @return {@code Counts} object which represents the file result
	 */
	public Counts get(final File file) {
		int index = results.indexOf(new FileCount(file, null));

		if (index == -1) {
			return null;
		} else {
			return results.get(index).getCounts();
		}
	}

	/**
	 * Returns all saved filenames.
	 * @return filenames of all results.
	 */
	public File[] getFiles() {
		List<File> result = new ArrayList<>();
		for (FileCount fileCount : results) {
			result.add(fileCount.getFile());
		}

        Collections.sort(result, new FitFileComparator());
        return result.toArray(new File[result.size()]);
	}

	/**
	 * Returns the sum of all results.
	 * @return sum of all results
	 */
	public Counts getSummary() {
		Counts result = new Counts();
		for (FileCount fileCount : results) {
			if (fileCount.getCounts() != null) {
				result.tally(fileCount.getCounts());
			}
		}
		return result;
	}

	private static String color(final Counts counts) {
		if (counts == null) {
			return FitUtils.HTML_GREY;
		} else if (counts.exceptions > 0 || counts.wrong > 0) {
			return FitUtils.HTML_RED;
		} else {
			return FitUtils.HTML_GREEN;
		}
	}

	/**
	 * Returns a single HTML Table row representing the results of the file
	 * {@code file}.
	 * @param file filename to look up
	 * @return HTML String with a a single Table row
	 */
	public String getRow(final File file) {
		StringBuilder builder = new StringBuilder();

		Counts counts = get(file);

		builder.append("<tr bgcolor=\"");
		builder.append(color(counts));
		builder.append("\"><td>");

		int depth = dirHelper.dirDepth(file);
		indent(depth, builder);

		builder.append("<a href=\"");
		builder.append(htmlSafeFile(file));
		builder.append("\">");
		builder.append(file.getName());
		builder.append("</a>");
		builder.append("</td><td>");

		if (counts == null) {
			builder.append("(none)");
		} else {
			builder.append(counts.toString());
		}
		builder.append("</td></tr>");

		return builder.toString();
	}

	/**
	 * Returns a summary row for a whole test run.
	 * @param directory headline of the table
	 * @return a single HTML row
	 */
	public String getSummaryRow(final File directory) {
		StringBuilder builder = new StringBuilder();
		Counts counts = getSummary();

		builder.append("<tr bgcolor=\"");
		builder.append(color(counts));
		builder.append("\"><th style=\"text-align: left\">");
		builder.append(directory.getName());
		builder.append("</th><th style=\"text-align: left\">");
		builder.append(counts.toString());
		builder.append("</th></tr>");

		return builder.toString();
	}

	/**
	 * Prints out a table to {@code stream} which contains all saved
	 * results including all summary rows.
	 * @param directory headline of the table
	 * @param stream stream to write results to
	 * @throws IOException thrown by {@code stream} in case of problems
	 */
        @Override
	public void print(final File directory, final OutputStream stream) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(stream);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write("<table>");
		bw.write(getSummaryRow(directory));
		bw.write("<tr><td colspan=\"2\"></td></tr>");

		File[] files = getFiles();
		if (files.length == 0) {
			bw.write("<tr><td colspan=\"2\">no files found</td></tr>");
		} else {
			File currentDir = directory;

			for (File file : files) {
                File newDir = file.getAbsoluteFile().getParentFile();
				if (!newDir.equals(currentDir) && !dirHelper.isSubDir(currentDir, newDir)) {
                    for (File tmpDir : dirHelper.getParentDirs(dirHelper.getCommonDir(currentDir, file), newDir)) {
                        bw.write(getSubSummaryRow(tmpDir));
                    }
                }
				currentDir = newDir;

				bw.write(getRow(file));
			}
		}
		bw.write("</table>");

		bw.flush();
		osw.flush();
	}

	/**
	 * Generates a HTML summary row for a subdirectory.
	 *
	 * @param path subdirectory to process
	 * @return a single HTML row
	 * @throws IOException if path cannot be resolved correctly
	 */
	public String getSubSummaryRow(final File path) throws IOException {
		Counts sum = subDirSum(path);

		return String.format("<tr bgcolor=\"%s\"><th style=\"text-align: left\">%s</th><td>%s</td></tr>",
				color(sum), FixtureTools.htmlSafeFile(dirHelper.abs2rel(new File("").getAbsolutePath(), path.getAbsolutePath())), sum.toString());
	}

	private Counts subDirSum(final File fullPath) throws IOException {
		Counts sum = new Counts();

		for (FileCount fileCount : results) {
			String filePath = fileCount.getFile().getCanonicalFile().getParentFile().getCanonicalFile().getAbsolutePath();
			if (filePath.startsWith(fullPath.getCanonicalFile().getAbsolutePath()) && fileCount.getCounts() != null) {
				sum.tally(fileCount.getCounts());
			}
		}

		return sum;
	}

	private void indent(final int depth, final StringBuilder builder) {
		for (int i = 0; i < depth; ++i) {
			builder.append(" &nbsp; &nbsp; &nbsp; &nbsp;");
		}
	}
}
