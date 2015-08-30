/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.FixtureRunner;
import de.cologneintelligence.fitgoodies.alias.AliasEnabledFixtureRunner;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.htmlparser.FitDocument;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

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
	 * Processes {@code inputFile}, write output to {@code outputFile}
	 * and return the resulting counts using most of fit's default components.
	 *
	 * @param inputFile  file to process
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

		helper.setFile(dirHelper.rel2abs(System.getProperty("user.dir"),
				inputFile.toString()));
		helper.setResultFile(dirHelper.rel2abs(System.getProperty("user.dir"),
				outputFile.toString()));
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

	@Override
	public final String getEncoding() {
		return encoding;
	}

	private Counts process(final File inputFile, final File outputFile) throws IOException {
		FixtureRunner fixtureRunner;
        fixtureRunner = prepareFixture(inputFile, outputFile);

        FitDocument document;
        try(InputStream is = new FileInputStream(inputFile)) {
            document = FitDocument.parse(is, encoding);
        }

        String result;
        try {
            fixtureRunner.doDocument(document);
            result = document.getHtml();
        } catch (Exception e) {
            result = "<html><head><title>Error</title></head><body>" +
                    "<p>Unable to parse input. Input ignored</p></body></html>";

            fixtureRunner.counts.right = 0;
            fixtureRunner.counts.wrong = 0;
            fixtureRunner.counts.ignores = 0;
            fixtureRunner.counts.exceptions = 1;
        }

        try (PrintWriter output = new PrintWriter(outputFile, encoding)) {
            output.print(result);
        }

		return fixtureRunner.counts;
	}

	@SuppressWarnings("unchecked")
	private FixtureRunner prepareFixture(final File in, final File out) {
		FixtureRunner fixtureRunner = new AliasEnabledFixtureRunner();
		fixtureRunner.summary.put("input file", in.getAbsolutePath());
		fixtureRunner.summary.put("input update", new Date(in.lastModified()));
		fixtureRunner.summary.put("output file", out.getAbsolutePath());
		return fixtureRunner;
	}
}
