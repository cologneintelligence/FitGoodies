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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.DirectoryProvider;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryProvider;
import de.cologneintelligence.fitgoodies.file.IteratorHelper;
import de.cologneintelligence.fitgoodies.file.RecursiveFileSelector;
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
 */
public class FitRunner {
    private final DirectoryProvider directoryProvider;
    private final String destPath;
    private final String encoding;
    private final AbstractDirectoryHelper directoryHelper;

    /**
     * Initializes a new FitRunner.
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
        this.directoryHelper = directoryHelper;
        this.directoryProvider = directory;

        destPath = unifySeperator(dest);
        encoding = fileEncoding;
    }

    /**
     * Generates a sorted list of all HTML relevant files in the input
     * directory.
     *
     * @return list of files.
     */
    public final FileInformation[] getRelevantFiles() {
        final List<FileInformation> files = new LinkedList<FileInformation>();
        final RecursiveFileSelector selector = new RecursiveFileSelector(
                directoryProvider, ".*\\.(?i:html?)");

        for (final FileInformation fi : new IteratorHelper<FileInformation>(selector)) {
            files.add(fi);
        }

        final FileInformation[] filearr = new FileInformation[files.size()];
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
        for (final FileInformation file : fileInformations) {
            directoryHelper.mkDir(directoryHelper.join(
                    destPath,
                    directoryHelper.removePrefix(file.pathname(),
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
        final FileInformation[] files = getRelevantFiles();
        prepareDirectories(files);

        return runFiles(fileRunner, result, log, files);
    }

    public boolean runFiles(final Runner fileRunner, final FitResult result,
            final PrintStream log, final FileInformation[] files) {
        boolean failed = false;
        for (final FileInformation file : files) {
            final String relPath = directoryHelper.abs2rel(directoryProvider.getPath(),
                    file.fullname());

            if (log != null) {
                log.println(relPath);
            }

            final Counts counts = fileRunner.run(file.fullname(),
                    directoryHelper.join(destPath, relPath));

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
    public final boolean runDir() throws IOException {
        final FitResultTable result = new FitResultTable(directoryHelper);

        final Runner runner = new FitFileRunner();
        runner.setEncoding(encoding);
        final boolean results = runFiles(runner, result, System.err);

        writeResults(result);

        return results;
    }

    private void writeResults(final FitResultTable result) throws FileNotFoundException, IOException {
        final FileOutputStream fos = new FileOutputStream(directoryHelper.join(destPath,
                "report.html"));
        final PrintWriter pw = new PrintWriter(fos, true);
        pw.println("<html><head><title>Fit Report</title></head><body>");
        pw.println("<h1>Fit Report</h1>");
        pw.println("<p>" + DateFormat.getDateTimeInstance().format(new Date())
                + "</p>");

        result.print(directoryHelper.abs2rel(System.getProperty("user.dir"),
                directoryProvider.getPath()), fos);

        pw.println("</body></html>");
        pw.close();
        fos.close();
    }


    private boolean runSingleFile(final Collection<String> files) throws IOException {
        boolean error = false;
        final Runner runner = new FitFileRunner();
        final FitResultTable table = new FitResultTable(directoryHelper);
        for (String file : files) {
            file = unifySeperator(file);
            System.out.println(file);
            final String outputFilePath = new File(destPath,
                    new File(file).getName()).getPath();
            System.out.println(outputFilePath);
            runner.setEncoding(encoding);
            final Counts result = runner.run(file, outputFilePath);
            System.out.println(result);
            if (result != null
                    && (result.exceptions > 0 || result.wrong > 0)) {
                error = true;
            }

            table.put(directoryHelper.abs2rel(destPath, outputFilePath), result);
        }

        writeResults(table);

        return error;
    }

    private boolean runDirWithOnly(final String sourcePath,
            List<String> selectedFiles) throws IOException {
        boolean error = false;
        final FitResultTable resultTable = new FitResultTable(directoryHelper);
        final Runner runner = new FitFileRunner();
        selectedFiles = addMaintenanceFiles(selectedFiles, sourcePath);

        for (String fileName : selectedFiles) {
            final File outputFile = new File(new File(destPath), fileName);
            outputFile.getParentFile().mkdirs();
            final String outputFilePath = outputFile.getPath();
            runner.setEncoding(encoding);
            System.out.println(outputFilePath);
            final String filePath = new File(new File(sourcePath), fileName).getPath();
            fileName = unifySeperator(filePath);
            final Counts result = runner.run(fileName, outputFilePath);
            System.out.println(result);

            if (result != null
                    && (result.exceptions > 0 || result.wrong > 0)) {
                error = true;
            }

            resultTable.put(directoryHelper.abs2rel(sourcePath, filePath), result);
        }

        writeResults(resultTable);
        return error;
    }

    private String unifySeperator(final String destPath) {
        return unifySeparator(directoryHelper, destPath);
    }

    private static String unifySeparator(final AbstractDirectoryHelper directoryHelper, String destPath) {
        destPath = directoryHelper.rel2abs(
                System.getProperty("user.dir"),
                destPath.replace('/', File.separatorChar).replace('\\',
                        File.separatorChar));
        return destPath;
    }

    private ArrayList<String> addMaintenanceFiles(
            final List<String> selectedFiles, final String sourcePath) {
        Collections.sort(selectedFiles);
        ArrayList<String> filesWithSetupTeardown = new ArrayList<String>();
        final FixtureFileListBuilder builder = new FixtureFileListBuilder(sourcePath);
        for (final String fileName : selectedFiles) {
            builder.addFile(fileName);
        }
        filesWithSetupTeardown = (ArrayList<String>) builder.returnFiles();
        return filesWithSetupTeardown;
    }

    public static void main(final String[] args) throws Exception {
        /*
         * Parse the provided arguments with jargs
         */
        final OptionParser parser = new OptionParser();
        parser.accepts("encoding").withRequiredArg().defaultsTo("utf-8");
        parser.accepts("source").withRequiredArg();
        parser.accepts("destination").withRequiredArg();
        parser.accepts("file").withOptionalArg();
        parser.accepts("only").withOptionalArg();

        final OptionSet options = parser.parse(args);

        final String encoding = options.valueOf("encoding").toString();

        String sourcePath = (String) options.valueOf("source");
        final String destPath = (String) options.valueOf("destination");
        @SuppressWarnings("unchecked")
        final List<String> files = (List<String>) options.valuesOf("file");
        @SuppressWarnings("unchecked")
        final List<String> selectedFiles = (List<String>) options
                .valuesOf("only");

        if (args.length < 2 || (sourcePath != null && files.size() > 0)) {
            final String usage = "FitRunner {-d dir | --destination dir } [-e encoding | --encoding encoding] {-f file | -s dir [--only file1 ... fileN]}";
            System.err.println("Usage: " + usage);
            throw new RuntimeException(usage);
        }

        final FileSystemDirectoryHelper directoryHelper = new FileSystemDirectoryHelper();

        if (sourcePath != null) {
            sourcePath = unifySeparator(directoryHelper, sourcePath);
        } else {
            sourcePath = unifySeparator(directoryHelper, System.getProperty("user.dir"));
        }

        final FitRunner runner = new FitRunner(
                new FileSystemDirectoryProvider(sourcePath), destPath,
                encoding, directoryHelper);

        final boolean error;

        /*
         * --only was used, the additional setup and teardown files will be
         * added
         */
        if (selectedFiles != null && selectedFiles.size() > 0) {
            error = runner.runDirWithOnly(sourcePath, new ArrayList<String>(
                    selectedFiles));
        }

        /*
         * -f was used, run the specified files.
         */
        else if (files != null && files.size() > 0) {
            error = runner.runSingleFile(files);
        }

        /*
         * -s was used without --only, include all files from sourcePath
         */
        else {
            error = runner.runDir();
        }

        if (error) {
            System.exit(1);
        }
    }
}
