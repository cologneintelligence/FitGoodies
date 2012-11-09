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


package de.cologneintelligence.fitgoodies.runners;

import java.text.ParseException;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryProvider;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import fit.Counts;
import fit.Parse;

/**
 * Run sub-fixtures.<br /><br />
 *
 * This fixture allows to start other fixtures and collects their results.
 * The files must be specified relative to the fixtures file path.<br /><br />
 *
 * Example:
 * <table border="1" colspan="2"><tr><td>fitgoodies.runners.RunFixture</td></tr>
 * <tr><td>file</td><td>file1.html</td></tr>
 * <tr><td>file</td><td>dir/file2.html</td></tr>
 * <tr><td>directory</td><td>other_tests/</td></tr>
 * </table>
 *
 * @author jwierum
 */
public class RunFixture extends ActionFixture {
    private Runner runner;
    private AbstractDirectoryHelper dirHelper;
    private String thisdir;
    private String outdir;
    private Parse thisRow;
    private RunnerHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        helper = DependencyManager.INSTANCE.getOrCreate(RunnerHelper.class);
        dirHelper = helper.getHelper();
        thisdir = dirHelper.getDir(helper.getFilePath());
        outdir = dirHelper.getDir(helper.getResultFilePath());
        runner = helper.getRunner();
    }

    @Override
    public void doRows(final Parse rows) {
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
            return red;
        } else {
            return green;
        }
    }

    private void generateResultRow(
            final Parse firstCell,
            final String name,
            final Counts results)
                    throws ParseException {

        firstCell.more = new Parse("<td></td><td></td>", new String[]{"td"});
        firstCell.more.body = "<a href=\"" + name + "\">" + name + "</a>";

        //Parse newCell = new Parse("<td></td>", new String[]{"td"});
        firstCell.more.more.body = results.toString();
        firstCell.more.more.addToTag(" bgcolor=\"" + color(results) + "\"");
        //firstCell.more = newCell;
    }

    /**
     * Runs the file <code>fileName</code> using the current runner and replaces
     * the current row with the results.
     * @param fileName file to process
     * @throws Exception propagated to fit
     */
    public final void file(final String fileName) throws Exception {
        String in = dirHelper.rel2abs(thisdir, fileName);
        String out = dirHelper.rel2abs(outdir, fileName);

        dirHelper.mkDir(dirHelper.getDir(out));
        Counts result = runner.run(in, out);

        generateResultRow(cells, fileName, result);
        counts.tally(result);
    }

    /**
     * Runs all HTML files in <code>dir</code> using the current runner and replaces
     * the current row with the results.
     * @param dir file to process
     * @throws Exception propagated to fit
     */
    public final void directory(final String dir) throws Exception {
        String srcDir = dirHelper.rel2abs(thisdir, dir);

        FitParseResult results = new FitParseResult();

        DirectoryRunner directoryRunner = new DirectoryRunner(
                new FileSystemDirectoryProvider(srcDir),
                outdir, helper.getRunner().getEncoding(),
                dirHelper);

        directoryRunner.runFiles(helper.getRunner(), results,
                helper.getLog());

        results.replaceLine(thisRow);
        counts.tally(results.getCounts());
    }
}
