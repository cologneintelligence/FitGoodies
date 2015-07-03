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

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * This runner traverses a directory tree. All files that end with .htm or .html
 * are processed. Files which are named setup.html are processed as the first
 * file in the directory, files which are named teardown.html are processed as
 * last. These files are <em>not</em> processed before each html file.<br />
 * <br />
 *
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

    private static String canonical(final FileSystemDirectoryHelper directoryHelper, String destPath) {
        destPath = directoryHelper.rel2abs(
                System.getProperty("user.dir"),
                destPath.replace('/', File.separatorChar).replace('\\', File.separatorChar));
        return destPath;
    }

    public static void main(final String[] args) throws IOException {
        ArgumentParser parser = new ArgumentParser(new File("."), new FileSystemDirectoryHelper());

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

            try {
                System.exit(1);
            } catch(SecurityException ignored) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        FileSystemDirectoryHelper directoryHelper = new FileSystemDirectoryHelper();

        RunConfiguration runConfiguration = new RunConfiguration();
        runConfiguration.setSource(parser.getFiles().toArray(new FileInformation[parser.getFiles().size()]));
        runConfiguration.setDestination(canonical(directoryHelper, parser.getDestinationDir()));
        runConfiguration.setEncoding(parser.getEncoding());

        final FitRunner fitRunner = new FitRunner(directoryHelper, runConfiguration);
        FitResultTable result = new FitResultTable(directoryHelper);
        boolean error = fitRunner.run(result);

        fitRunner.writeResults(result);

        if (error) {
            System.exit(1);
        }
    }

    public boolean run(FitResultTable resultTable) {
        boolean error = false;
        for (FileInformation file : runConfiguration.getSources()) {
            File outputFile = new File(new File(runConfiguration.getDestination()), file.getFile().getPath());
            outputFile.getParentFile().mkdirs();

            String filePath = new File(file.getFile(), outputFile.getPath()).getPath();

            System.out.println(outputFile.getPath());

            FitFileRunner runner = new FitFileRunner();
            runner.setEncoding(runConfiguration.getEncoding());
            Counts result = runner.run(file.getFile(), outputFile);

            System.out.println(result);

            if (result != null && (result.exceptions > 0 || result.wrong > 0)) {
                error = true;
            }

            resultTable.put(new File(directoryHelper.abs2rel(file.getFile().getName(), filePath)), result);
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
