/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.runners;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fit.Counts;
import fitgoodies.file.AbstractDirectoryHelper;

/**
 * Implementation of FitResult which generates a indented HTML table.
 *
 * @author jwierum
 * @version $Id$
 */
public final class FitResultTable implements FitResult {
	private final List<FileCount> results = new LinkedList<FileCount>();
	private final AbstractDirectoryHelper dirHelper;

	/**
	 * Generates a new Object.
	 * @param helper helper object to convert pathnames
	 */
	public FitResultTable(final AbstractDirectoryHelper helper) {
		dirHelper = helper;
	}

	/**
	 * Saves the result <code>result</code> of the file <code>file</code>.
	 * @param file filename to identify the result
	 * @param result results
	 */
	public void put(final String file, final Counts result) {
		FileCount fileCount = new FileCount(file, result);

		if (results.contains(fileCount)) {
			results.remove(fileCount);
		}

		results.add(fileCount);
	}

	/**
	 * Returns the <code>Counts</code> of a filename.
	 * @param file filename to look up
	 * @return <code>Counts</code> object which represents the file result
	 */
	public Counts get(final String file) {
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
	public String[] getFiles() {
		List<String> result = new ArrayList<String>();
		for (FileCount fileCount : results) {
			result.add(fileCount.getFile());
		}
		return result.toArray(new String[]{});
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
			return fit.Fixture.gray;
		} else if (counts.exceptions > 0 || counts.wrong > 0) {
			return fit.Fixture.red;
		} else {
			return fit.Fixture.green;
		}
	}

	/**
	 * Returns a single HTML Table row representing the results of the file
	 * <code>file</code>.
	 * @param file filename to look up
	 * @return HTML String with a a single Table row
	 */
	public String getRow(final String file) {
		StringBuilder builder = new StringBuilder();

		Counts counts = get(file);
		int depth = dirHelper.dirDepth(file);

		builder.append("<tr bgcolor=\"");
		builder.append(color(counts));
		builder.append("\"><td>");

		indent(depth, builder);

		builder.append("<a href=\"");
		builder.append(file);
		builder.append("\">");
		builder.append(dirHelper.getFilename(file));
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
	public String getSummaryRow(final String directory) {
		StringBuilder builder = new StringBuilder();
		Counts counts = getSummary();

		builder.append("<tr bgcolor=\"");
		builder.append(color(counts));
		builder.append("\"><th style=\"text-align: left\">");
		builder.append(directory);
		builder.append("</th><th style=\"text-align: left\">");
		builder.append(counts.toString());
		builder.append("</th></tr>");

		return builder.toString();
	}

	/**
	 * Prints out a table to <code>stream</code> which contains all saved
	 * results including all summary rows.
	 * @param directory headline of the table
	 * @param stream stream to write results to
	 * @throws IOException thrown by <code>stream</code> in case of problems
	 */
	public void print(final String directory,
			final OutputStream stream) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(stream);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write("<table>");
		bw.write(getSummaryRow(directory));
		bw.write("<tr><td colspan=\"2\"></td></tr>");

		String[] files = getFiles();
		if (files.length == 0) {
			bw.write("<tr><td colspan=\"2\">no files found</td></tr>");
		} else {
			String dir = ""; //dirHelper.getDir(files[0]);

			for (String file : files) {
				String newDir = dirHelper.getDir(file);
				if (newDir != null) {
	    			if (!newDir.equals(dir) && !dirHelper.isSubDir(dir, newDir)) {
	    				for (String tmpDir : dirHelper.getParentDirs(
	    						dirHelper.getCommonDir(dir, newDir), newDir)) {
	    					bw.write(getSubSummaryRow(tmpDir));
	    				}
	    			}
	    			dir = newDir;
				}
				bw.write(getRow(file));
			}
		}
		bw.write("</table>");

		bw.flush();
		osw.flush();
	}

	/**
	 * Generates a HTML summary row for a subdirectory.
	 * @param path subdirectory to process
	 * @return a single HTML row
	 */
	public String getSubSummaryRow(final String path) {
		String fullPath = path;
		if (!path.endsWith(dirHelper.separator())) {
			fullPath += dirHelper.separator();
		}

		Counts sum = subDirSum(fullPath);

		//int depth = dirHelper.dirDepth(fullPath) - 1;

		StringBuilder builder = new StringBuilder();
		builder.append("<tr bgcolor=\"");
		builder.append(color(sum));
		builder.append("\"><th style=\"text-align: left\">");

		//indent(depth, builder);

		builder.append(fullPath);
		builder.append("</th><td>");
		builder.append(sum.toString());
		builder.append("</td></tr>");

		return builder.toString();
	}

	private Counts subDirSum(final String fullPath) {
		Counts sum = new Counts();

		for (FileCount fileCount : results) {
			String filePath = dirHelper.getDir(fileCount.getFile());
			if (filePath.startsWith(fullPath) && fileCount.getCounts() != null) {
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
