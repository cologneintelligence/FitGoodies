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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fit.Counts;
import fitgoodies.file.AbstractDirectoryHelper;
import fitgoodies.file.DirectoryProvider;
import fitgoodies.file.FileInformation;
import fitgoodies.file.FileSystemDirectoryHelper;
import fitgoodies.file.FileSystemDirectoryProvider;
import fitgoodies.file.IteratorHelper;
import fitgoodies.file.RecursiveFileSelector;

/**
 * This runner traverses a directory tree. All files that end with
 * .htm or .html are processed. Files which are named setup.html are processed
 * as the first file in the directory, files which are named teardown.html are
 * processed as last. These files are <em>not</em> processed before each html
 * file.<br /><br />
 *
 * All processed files are copied into an output folder. Additionally, a report
 * file is generated.
 *
 * @author jwierum
 * @version $Id$
 */
public class DirectoryRunner {
	private final DirectoryProvider directoryProvider;
	private final String destPath;
	private final String encoding;
	private final AbstractDirectoryHelper helper;

	/**
	 * Initializes a new DirectoryRunner.
	 * @param directory provider, which represents the selected input directory.
	 * @param dest destination directory (absolute path)
	 * @param fileEncoding encoding used to read the input files
	 * @param directoryHelper helper object to manage pathes and pathnames
	 */
	public DirectoryRunner(final DirectoryProvider directory,
			final String dest,
			final String fileEncoding,
			final AbstractDirectoryHelper directoryHelper) {
		directoryProvider = directory;

		destPath = dest;
		encoding = fileEncoding;
		helper = directoryHelper;
	}

	/**
	 * Generates a sorted list of all HTML relevant files in the input directory.
	 * @return list of files.
	 */
	public final FileInformation[] getRelevantFiles() {
		List<FileInformation> files = new LinkedList<FileInformation>();
		RecursiveFileSelector selector =
			new RecursiveFileSelector(directoryProvider, ".*\\.(?i:html?)");

		for (FileInformation fi : new IteratorHelper<FileInformation>(selector)) {
			files.add(fi);
		}

		FileInformation[] filearr = new FileInformation[files.size()];
		files.toArray(filearr);

		Arrays.sort(filearr, new FileNameComperator());
		return filearr;
	}

	/**
	 * Creates the directory structure which is needed to save all files in
	 * <code>fileInformation</code>.
	 * @param fileInformations list of files to process
	 */
	public final void prepareDirectories(
			final FileInformation[] fileInformations) {
		for (FileInformation file : fileInformations) {
			helper.mkDir(helper.join(destPath,
					helper.removePrefix(file.pathname(),
							directoryProvider.getPath())));
		}
	}

	/**
	 * Processes a directory with a given Runner, writing results to
	 * <code>result</code>, printing output to <code>log</code>.
	 *
	 * @param fileRunner runner which will process the input files
	 * @param result FitResult object, which collects the results
	 * @param log stream to print log messages to. If <code>log</code> is
	 * 		<code>null</code>, nothing is logged.
	 */
	public final boolean runFiles(
			final Runner fileRunner,
			final FitResult result,
			final PrintStream log) {
		FileInformation[] files = getRelevantFiles();
		prepareDirectories(files);
		
		return !runFiles(fileRunner, result, log, files);
	}

	public boolean runFiles(final Runner fileRunner, final FitResult result, final PrintStream log, FileInformation[] files) {
		boolean failed = false;
		for (FileInformation file : files) {
			String relPath = helper.abs2rel(directoryProvider.getPath(), file.fullname());

			if (log != null) {
				log.println(relPath);
			}

			Counts counts = fileRunner.run(file.fullname(),
					helper.join(destPath, relPath));

			if (counts != null && (counts.exceptions > 0 || counts.wrong > 0)) {
				failed = true;
			}
			
			if (log != null) {
				log.println(counts);
			}

			if (result != null) {
				result.put(relPath, counts);
			}
		}
		return failed;
	}

	/**
	 * Prepares the output and runs
	 * {@link #runFiles(Runner, FitResult, PrintStream)}.
	 *
	 * The normal file system will be used. The report is saved as
	 * &quot;report.html&quot; in the output directory.
	 * @throws IOException thrown if a file access went wrong
	 */
	public final boolean runStandAlone() throws IOException {
		FitResultTable result = new FitResultTable(helper);

		Runner runner = new FitFileRunner();
		runner.setEncoding(encoding);
		boolean results = runFiles(runner,
				result, System.err);

		FileOutputStream fos = new FileOutputStream(
				helper.join(destPath, "report.html"));
		PrintWriter pw = new PrintWriter(fos, true);
		pw.println("<html><head><title>Fit Report</title></head><body>");
		pw.println("<h1>Fit Report</h1>");
		pw.println("<p>" + DateFormat.getDateTimeInstance().format(new Date()) + "</p>");

		result.print(helper.abs2rel(System.getProperty("user.dir"),
				directoryProvider.getPath()), fos);

		pw.println("</body></html>");
		pw.close();
		fos.close();
		
		return results;
	}

	/**
	 * Entry point.
	 * Takes 2 or 3 arguments, either the input directory and the output directory,
	 * or the input directory, the output directory and the encoding. If the encoding
	 * is omitted, utf-8 is used.
	 *
	 * @param args program parameters
	 */
	public static void main(final String[] args) {
		if (args.length < 2) {
			final String error = "Usage:\n"
				+ "fitgoodies.runners.DirectoryRunner inputdir outputdir [encoding]";
			System.err.println(error);
			throw new RuntimeException(error);
		}

		try {
			String encoding = "utf-8";

			if (args.length > 2) {
				encoding = args[2];
			}

			AbstractDirectoryHelper directoryHelper = new FileSystemDirectoryHelper();
			String destPath = directoryHelper.rel2abs(System.getProperty("user.dir"),
					args[1].replace('/', File.separatorChar).replace('\\', File.separatorChar));
			String sourcePath = directoryHelper.rel2abs(System.getProperty("user.dir"),
					args[0].replace('/', File.separatorChar).replace('\\', File.separatorChar));

			DirectoryRunner runner = new DirectoryRunner(
					new FileSystemDirectoryProvider(sourcePath),
					destPath, encoding, directoryHelper);
			
			if (!runner.runStandAlone()) {
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
