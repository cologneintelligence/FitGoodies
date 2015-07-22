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

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Counts;
import fit.FitUtils;
import fit.Parse;

import java.io.File;
import java.text.ParseException;
import java.util.List;

/**
 * Run sub-fixtures.
 *
 * This fixture allows to start other fixtures and collects their results.
 * The files must be specified relative to the fixtures file path.
 * <p>
 * Example:
 * <p>
 *
 * <table border="1" summary=""><tr><td>fitgoodies.runners.RunFixture</td></tr>
 * <tr><td>file</td><td>file1.html</td></tr>
 * <tr><td>file</td><td>dir/file2.html</td></tr>
 * <tr><td>directory</td><td>other_tests/</td></tr>
 * </table>
 *
 */
public class RunFixture extends ActionFixture {
    private Runner runner;
    private FileSystemDirectoryHelper dirHelper;
    private File thisDir;
    private File outDir;
    private Parse thisRow;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        RunnerHelper helper = DependencyManager.getOrCreate(RunnerHelper.class);
        dirHelper = helper.getHelper();
        thisDir = helper.getFile().getParentFile();
        outDir = helper.getResultFile().getParentFile();
        runner = helper.getRunner();
    }

    @Override
    protected void doRows(final Parse rows) {
        Parse nextRow = rows;

        while (nextRow != null) {
            thisRow = nextRow;
            nextRow = nextRow.more;
            super.doRow(thisRow);
        }
    }

    /**
     * Calls {@link #file(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void file() throws Exception {
        transformAndEnter();
    }

    /**
     * Calls {@link #directory(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void directory() throws Exception {
        transformAndEnter();
    }

    private String color(final Counts c) {
        if (c.wrong > 0 || c.exceptions > 0) {
            return FitUtils.HTML_RED;
        } else {
            return FitUtils.HTML_GREEN;
        }
    }

    private void generateResultRow(
            final Parse firstCell,
            final String name,
            final Counts results)
                    throws ParseException {

        firstCell.more = new Parse("<td></td><td></td>", new String[]{"td"});
        firstCell.more.body = "<a href=\"" + name + "\">" + name + "</a>";

        firstCell.more.more.body = results.toString();
        firstCell.more.more.addToTag(" bgcolor=\"" + color(results) + "\"");
    }

    /**
     * Runs the file <code>fileName</code> using the current runner and replaces
     * the current row with the results.
     * @param fileName file to process
     * @throws Exception propagated to fit
     */
    public void file(final String fileName) throws Exception {
        String in = thisDir.getAbsolutePath();
        File out = outDir.getAbsoluteFile();

        //noinspection ResultOfMethodCallIgnored
        out.mkdirs();

        File inputFile = dirHelper.rel2abs(in, fileName);
        File outputFile = dirHelper.subdir(out, inputFile.getName());
        Counts result = runner.run(inputFile, outputFile);

        generateResultRow(cells, inputFile.getName(), result);
        counts().tally(result);
    }

    /**
     * Runs all HTML files in <code>dir</code> using the current runner and replaces
     * the current row with the results.
     * @param dir file to process
     * @throws Exception propagated to fit
     */
    // TODO: not tested?
    public void directory(final String dir) throws Exception {
        File srcDir = dirHelper.rel2abs(thisDir.getAbsolutePath(), dir);

        List<FileInformation> files = new DirectoryFilter(srcDir, dirHelper).getSelectedFiles();

        RunConfiguration runConfiguration = new RunConfiguration();
        runConfiguration.setEncoding(runner.getEncoding());
        runConfiguration.setBaseDir(srcDir);
        runConfiguration.setDestination(outDir.getPath());
        runConfiguration.setSource(files.toArray(new FileInformation[files.size()]));
        System.out.println("Run: " + files + " in " + srcDir + " to " + outDir);

        final FitRunner fitRunner = new FitRunner(dirHelper, runConfiguration);

        FitParseResult results = new FitParseResult();
        fitRunner.run(results);
        results.replaceLastIn(thisRow);
        counts().tally(results.getCounts());
    }
}
