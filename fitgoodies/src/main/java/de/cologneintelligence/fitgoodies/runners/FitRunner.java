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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.IllegalOptionValueException;
import com.sanityinc.jargs.CmdLineParser.Option;
import com.sanityinc.jargs.CmdLineParser.OptionException;

import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.DirectoryProvider;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryProvider;
import de.cologneintelligence.fitgoodies.file.IteratorHelper;
import de.cologneintelligence.fitgoodies.file.RecursiveFileSelector;
import de.cologneintelligence.fitgoodies.file.builder.FixtureFileListBuilder;
import fit.Counts;

/**
 * This runner traverses a directory tree. All files that end with .htm or .html
 * are processed. Files which are named setup.html are processed as the first
 * file in the directory, files which are named teardown.html are processed as
 * last. These files are <em>not</em> processed before each html file.<br />
 * <br />
 * 
 * All processed files are copied into an output folder. Additionally, a report
 * file is generated.
 * 
 * @author dgoering
 * @version $Id$
 */
public class FitRunner {
	private final DirectoryProvider directoryProvider;
	private final String destPath;
	private final String encoding;
	private final AbstractDirectoryHelper helper;
	private static AbstractDirectoryHelper directoryHelper;

	/**
	 * Initializes a new DirectoryRunner.
	 * 
	 * @param directory
	 *            provider, which represents the selected input directory.
	 * @param dest
	 *            destination directory (absolute path)
	 * @param fileEncoding
	 *            encoding used to read the input files
	 * @param directoryHelper
	 *            helper object to manage pathes and pathnames
	 */
	public FitRunner(final DirectoryProvider directory, final String dest,
			final String fileEncoding,
			final AbstractDirectoryHelper directoryHelper) {
		directoryProvider = directory;

		destPath = dest;
		encoding = fileEncoding;
		helper = directoryHelper;
	}

	/**
	 * Generates a sorted list of all HTML relevant files in the input
	 * directory.
	 * 
	 * @return list of files.
	 */
	public final FileInformation[] getRelevantFiles() {
		List<FileInformation> files = new LinkedList<FileInformation>();
		RecursiveFileSelector selector = new RecursiveFileSelector(
				directoryProvider, ".*\\.(?i:html?)");

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
	 * 
	 * @param fileInformations
	 *            list of files to process
	 */
	public final void prepareDirectories(
			final FileInformation[] fileInformations) {
		for (FileInformation file : fileInformations) {
			helper.mkDir(helper.join(
					destPath,
					helper.removePrefix(file.pathname(),
							directoryProvider.getPath())));
		}
	}

	/**
	 * Processes a directory with a given Runner, writing results to
	 * <code>result</code>, printing output to <code>log</code>.
	 * 
	 * @param fileRunner
	 *            runner which will process the input files
	 * @param result
	 *            FitResult object, which collects the results
	 * @param log
	 *            stream to print log messages to. If <code>log</code> is
	 *            <code>null</code>, nothing is logged.
	 */
	public final boolean runFiles(final Runner fileRunner,
			final FitResult result, final PrintStream log) {
		FileInformation[] files = getRelevantFiles();
		prepareDirectories(files);

		return !runFiles(fileRunner, result, log, files);
	}

	public boolean runFiles(final Runner fileRunner, final FitResult result,
			final PrintStream log, FileInformation[] files) {
		boolean failed = false;
		for (FileInformation file : files) {
			String relPath = helper.abs2rel(directoryProvider.getPath(),
					file.fullname());

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
	 * 
	 * @throws IOException
	 *             thrown if a file access went wrong
	 */
	public final boolean runStandAlone() throws IOException {
		FitResultTable result = new FitResultTable(helper);

		Runner runner = new FitFileRunner();
		runner.setEncoding(encoding);
		boolean results = runFiles(runner, result, System.err);

		FileOutputStream fos = new FileOutputStream(helper.join(destPath,
				"report.html"));
		PrintWriter pw = new PrintWriter(fos, true);
		pw.println("<html><head><title>Fit Report</title></head><body>");
		pw.println("<h1>Fit Report</h1>");
		pw.println("<p>" + DateFormat.getDateTimeInstance().format(new Date())
				+ "</p>");

		result.print(helper.abs2rel(System.getProperty("user.dir"),
				directoryProvider.getPath()), fos);

		pw.println("</body></html>");
		pw.close();
		fos.close();

		return results;
	}

	public static void main(final String[] args)
			throws IllegalOptionValueException {

		/*
		 * Parse the provided arguments with jargs
		 */
		directoryHelper = new FileSystemDirectoryHelper();
		CmdLineParser parser = new CmdLineParser();
		Option<String> enc = parser.addStringOption('e', "encoding");
		Option<String> sourceDir = parser.addStringOption('s', "source");
		Option<String> destinationDir = parser.addStringOption('d',
				"destination");
		Option<String> f = parser.addStringOption('f', "file");
		Option<String> onlyFiles = parser.addStringOption("only");

		try {
			parser.parse(args);
		} catch (OptionException e1) {
			throw new RuntimeException(e1);
		}

		String encoding = parser.getOptionValue(enc, "utf-8");
		String sourcePath = parser.getOptionValue(sourceDir);
		String destPath = parser.getOptionValue(destinationDir);
		Collection<String> files = parser.getOptionValues(f);
		String firstSelectedFile = parser.getOptionValue(onlyFiles);
		String[] otherArgs = parser.getRemainingArgs();
		ArrayList<String> selectedFiles = new ArrayList<String>();
		selectedFiles.add(firstSelectedFile);
		selectedFiles.addAll(Arrays.asList(otherArgs));

		if (args.length < 2) {
			String error = "Usage: FitRunner -d,--destination [-e, --encoding] {-f file | -s dir [--only file1 ... fileN]";
			System.err.println(error);
			throw new RuntimeException(error);
		}
		if (sourcePath != null && files.size() > 0) {
			String error = "FitRunner -d,--destination [-e, --encoding] {-f file | -s dir [--only file1 ... fileN]";
			System.err.println(error);
			throw new RuntimeException(error);
		}
		/*
		 * --only was used, the additional setup and teardown files will be
		 * added
		 */
		if (firstSelectedFile != null) {
			Runner runner = new FitFileRunner();
			destPath = unifySeperator(destPath);
			boolean error = false;
			selectedFiles = addMaintenanceFiles(selectedFiles, sourcePath);

			for (String fileName : selectedFiles) {
				File outputFile = new File(new File(destPath), fileName);
				outputFile.getParentFile().mkdirs();
				String outputFilePath = outputFile.getPath();
				runner.setEncoding(encoding);
				System.out.println(outputFilePath);
				String filePath = new File(new File(sourcePath), fileName)
				.getPath();
				fileName = unifySeperator(filePath);
				Counts result = runner.run(fileName, outputFilePath);
				System.out.println(result);
				if (result != null
						&& (result.exceptions > 0 || result.wrong > 0)) {
					error = true;
				}
			}
			if (error) {
				System.exit(1);
			}
		}
		/*
		 * -s was used without --only, include all files from sourcePath
		 */
		else if (sourcePath != null) {
			try {

				destPath = unifySeperator(destPath);
				sourcePath = unifySeperator(sourcePath);

				FitRunner runner = new FitRunner(
						new FileSystemDirectoryProvider(sourcePath), destPath,
						encoding, directoryHelper);

				if (!runner.runStandAlone()) {
					System.exit(1);
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
		/*
		 * -f was used, run the specified files.
		 */
		else if (files != null) {
			Runner runner = new FitFileRunner();
			boolean error = false;
			for (String file : files) {
				file = unifySeperator(file);
				destPath = unifySeperator(destPath);
				String outputFilePath = new File(destPath
						+ new File(file).getName()).getPath();
				runner.setEncoding(encoding);
				Counts result = runner.run(file, outputFilePath);
				System.out.println(result);
				if (result != null
						&& (result.exceptions > 0 || result.wrong > 0)) {
					error = true;
				}
			}
			if (error) {
				System.exit(1);
			}
		}
	}

	private static String unifySeperator(String destPath) {
		destPath = directoryHelper.rel2abs(
				System.getProperty("user.dir"),
				destPath.replace('/', File.separatorChar).replace('\\',
						File.separatorChar));
		return destPath;
	}

	private static ArrayList<String> addMaintenanceFiles(
			ArrayList<String> selectedFiles, String sourcePath) {
		java.util.Collections.sort(selectedFiles);
		ArrayList<String> filesWithSetupTeardown = new ArrayList<String>();
		FixtureFileListBuilder builder = new FixtureFileListBuilder(sourcePath);
		for (String fileName : selectedFiles) {
			builder.addFile(fileName);
		}
		filesWithSetupTeardown = (ArrayList<String>) builder.returnFiles();
		return filesWithSetupTeardown;
	}
}
