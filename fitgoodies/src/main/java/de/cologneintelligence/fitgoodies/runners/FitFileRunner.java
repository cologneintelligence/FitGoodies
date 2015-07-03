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

import de.cologneintelligence.fitgoodies.alias.AliasEnabledFixture;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Counts;
import fit.Fixture;
import fit.Parse;

import java.io.*;
import java.util.Date;

/**
 * Runs a test file using most of fit's default component.
 */
public class FitFileRunner implements Runner {
    private String encoding;

    @Override
    public void setEncoding(final String fileEncoding) {
        this.encoding = fileEncoding;
    }

    /**
     * Processes <code>inputFile</code>, write output to <code>outputFile</code>
     * and return the resulting counts using most of fit's default components.
     * @param inputFile file to process
     * @param outputFile file to write output to
     * @return resulting counts
     */
    @Override
    public final Counts run(final File inputFile, final File outputFile) {
        Counts result = null;

        FileSystemDirectoryHelper dirHelper = new FileSystemDirectoryHelper();
        RunnerHelper currentRunner = DependencyManager.getOrCreate(RunnerHelper.class);
        RunnerHelper helper = new RunnerHelper();

        DependencyManager.inject(RunnerHelper.class, helper);

        helper.setFile(new File(dirHelper.rel2abs(System.getProperty("user.dir"),
                inputFile.toString())));
        helper.setResultFile(new File(dirHelper.rel2abs(System.getProperty("user.dir"),
                outputFile.toString())));
        helper.setRunner(this);
        helper.setHelper(dirHelper);

        try {
            result = process(inputFile, outputFile);
        } catch (Exception e) {
            System.err.printf("%s while processing %s -> %s%n", e, inputFile, outputFile);
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DependencyManager.inject(RunnerHelper.class, currentRunner);
        }

        return result;
    }

    /**
     * Returns the content of the file <code>input</code> using the saved encoding.
     * @param input input filename
     * @return the file's content
     * @see #setEncoding(String) setEncoding(String)
     * @throws IOException if the file could not be read
     */
    // TODO: use apache commons?
    private String read(final File input) throws IOException {
        char[] chars = new char[(int) (input.length())];

        InputStream is = new FileInputStream(input);
        InputStreamReader ir = new InputStreamReader(is, encoding);
        ir.read(chars);
        ir.close();
        is.close();

        return new String(chars);
    }

    @Override
    public final String getEncoding() {
        return encoding;
    }

    private Counts process(final File inputFile, final File outputFile)
            throws IOException {
        PrintWriter output = new PrintWriter(outputFile, encoding);
        Fixture fixture = prepareFixture(inputFile, outputFile);

        String input = read(inputFile);
        Parse tables;

        try {
            if (input.contains("<wiki>")) {
                tables = new Parse(input, new String[]{"wiki", "table", "tr", "td"});
                fixture.doTables(tables.parts);
            } else {
                tables = new Parse(input, new String[]{"table", "tr", "td"});
                fixture.doTables(tables);
            }
        } catch (Exception e) {
            tables = new Parse("body", "Unable to parse input. Input ignored.", null, null);
            fixture.exception(tables, e);
        }
        tables.print(output);
        output.close();
        return fixture.counts;
    }

    @SuppressWarnings("unchecked")
    private Fixture prepareFixture(final File in, final File out) {
        Fixture fixture = new AliasEnabledFixture();
        fixture.summary.put("input file", in.getAbsolutePath());
        fixture.summary.put("input update", new Date(in.lastModified()));
        fixture.summary.put("output file", out.getAbsolutePath());
        return fixture;
    }
}
