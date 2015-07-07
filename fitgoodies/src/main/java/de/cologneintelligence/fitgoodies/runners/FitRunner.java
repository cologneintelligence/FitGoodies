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

import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import fit.Counts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * This runner traverses a directory tree. All files that end with .htm or .html
 * are processed. Files which are named setup.html are processed as the first
 * file in the directory, files which are named teardown.html are processed as
 * last. These files are <em>not</em> processed before each html file.<br />
 * <br />
 * <p/>
 * All processed files are copied into an output folder. Additionally, a report
 * file is generated.
 */
public class FitRunner {
    private final FileSystemDirectoryHelper directoryHelper;
    private final RunConfiguration runConfiguration;

    public FitRunner(FileSystemDirectoryHelper directoryHelper, RunConfiguration runConfiguration) {
        this.directoryHelper = directoryHelper;
        this.runConfiguration = runConfiguration;
    }

    private static String canonical(final FileSystemDirectoryHelper directoryHelper, File destPath) {
        return directoryHelper.rel2abs(
                System.getProperty("user.dir"),
                destPath.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar)).getPath();
    }

    public static void main(final String[] args) throws Throwable {
        ArgumentParser parser = new ArgumentParser(new File(System.getProperty("user.dir")),
                new FileSystemDirectoryHelper());

        try {
            parser.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("");
            System.err.println("Usage: FitRunner -d dir [-e encoding] [-f file] [-s dir [-o file1 ... fileN]]");
            System.err.println("");
            System.err.println("-d or --directory  Output directory");
            System.err.println("-e or --encoding   Input and output encoding [default: utf-8]");
            System.err.println("-f or --file       Parse a single file");
            System.err.println("-s or --source     Source Directory");
            System.err.println("-l or --limit      Only execute these files (in the last source)");
            System.err.println("                   Automatically includes setup and teardown");
            System.err.println("");
            System.err.println("-f, -s and -l can be applied multiple times");
            System.err.println("At least one -f or -s must be provided");

            abort(parser, new IllegalArgumentException(e.getMessage()));
        }

        FileSystemDirectoryHelper directoryHelper = new FileSystemDirectoryHelper();

        RunConfiguration runConfiguration = new RunConfiguration();
        List<FileInformation> files = parser.getFiles();
        runConfiguration.setSource(files.toArray(new FileInformation[files.size()]));
        runConfiguration.setEncoding(parser.getEncoding());
        runConfiguration.setDestination(parser.getDestinationDir().getAbsolutePath());
        runConfiguration.setBaseDir(parser.getBaseDir());

        final FitRunner fitRunner = new FitRunner(directoryHelper, runConfiguration);
        FitResultTable result = new FitResultTable(directoryHelper);
        boolean error = fitRunner.run(result);

        fitRunner.writeResults(result);

        if (error) {
            abort(parser, new AssertionError("Tests failed"));
        }
    }

    private static void abort(ArgumentParser parser, Throwable t) throws Throwable {
        if (!parser.isNoExit()) {
            try {
                System.exit(1);
            } catch (SecurityException ignored) {
            }
        }
        throw t;
    }

    public boolean run(FitResult resultTable) throws IOException {
        boolean error = false;

        //noinspection ResultOfMethodCallIgnored
        new File(runConfiguration.getDestination()).mkdirs();

        for (FileInformation file : runConfiguration.getSources()) {

            File outputFile;
            String relativeDestination;

            if (directoryHelper.isSubDir(file.getFile().getAbsoluteFile(), runConfiguration.getBaseDir())) {
                String baseDir = runConfiguration.getBaseDir().getAbsolutePath();
                relativeDestination = directoryHelper.abs2rel(baseDir, file.getFile().getAbsolutePath());
                outputFile = new File(runConfiguration.getDestination(), relativeDestination);
            } else {
                relativeDestination = file.getFile().getName();
                outputFile = new File(runConfiguration.getDestination(), relativeDestination);
            }

            //noinspection ResultOfMethodCallIgnored
            outputFile.getParentFile().mkdirs();

            FitFileRunner runner = new FitFileRunner();
            runner.setEncoding(runConfiguration.getEncoding());
            Counts result = runner.run(file.getFile(), outputFile);

            System.out.println(result);
            resultTable.put(new File(relativeDestination), result);

            error = error || (result != null && (result.exceptions > 0 || result.wrong > 0));
        }

        return error;
    }

    private void writeResults(final FitResultTable result) throws IOException {
        final FileOutputStream fos = new FileOutputStream(new File(
                runConfiguration.getDestination(), "report.html"));

        final PrintWriter pw = new PrintWriter(fos, true);
        pw.println("<html><head><title>Fit Report</title></head><body>");
        pw.println("<h1>Fit Report</h1>");
        pw.println("<p>" + DateFormat.getDateTimeInstance().format(new Date()) + "</p>");

        result.print(new File(System.getProperty("user.dir")), fos);

        pw.println("</body></html>");
        pw.close();
        fos.close();
    }
}
