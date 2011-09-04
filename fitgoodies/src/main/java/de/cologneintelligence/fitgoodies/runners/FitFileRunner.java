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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

import de.cologneintelligence.fitgoodies.alias.AliasEnabledFixture;
import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;

import fit.Counts;
import fit.Fixture;
import fit.Parse;

/**
 * Runs a test file using most of fit's default component.
 *
 * @author jwierum
 * @version $Id$
 */
public class FitFileRunner implements Runner {
	private String encoding;

	@Override
	public final void setEncoding(final String fileEncoding) {
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
	public final Counts run(final String inputFile, final String outputFile) {
		AbstractDirectoryHelper helper = new FileSystemDirectoryHelper();

		RunnerHelper.instance().setFilePath(helper.rel2abs(System.getProperty("user.dir"),
				inputFile));
		RunnerHelper.instance().setResultFilePath(helper.rel2abs(System.getProperty("user.dir"),
				outputFile));
		RunnerHelper.instance().setRunner(this);
		RunnerHelper.instance().setHelper(helper);

		try {
			return process(inputFile, outputFile);
		} catch (Exception e) {
			System.err.println(e + " while processing " + inputFile + " ->"
					+ outputFile);
			System.err.println(e.getMessage());
			e.printStackTrace();

			return null;
		}
	}

    /**
     * Returns the content of the file <code>input</code> using the saved encoding.
     * @param input input filename
     * @return the file's content
     * @see #setEncoding(String) setEncoding(String)
     * @throws IOException if the file could not be read
     */
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

    private Counts process(final String inputFile, final String outputFile)
    		throws IOException {
    	File in = new File(inputFile);
    	File out = new File(outputFile);
    	PrintWriter output = new PrintWriter(out, encoding);
    	Fixture fixture = prepareFixture(in, out);

    	String input = read(in);
    	Parse tables;

        try {
            if (input.indexOf("<wiki>") >= 0) {
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
