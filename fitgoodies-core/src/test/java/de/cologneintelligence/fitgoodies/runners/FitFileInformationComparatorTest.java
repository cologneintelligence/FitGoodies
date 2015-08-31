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

import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FitFileInformationComparatorTest extends FitGoodiesTestCase {
	private final FitFileInformationComparator comp = new FitFileInformationComparator();

	private void assertCompares(final int sign,
	                            final String f1, final String f2) {

		FileInformation fi1 = new FileInformation(new File(f1));
		FileInformation fi2 = new FileInformation(new File(f2));

		final String message = "Unexpected result when comparing: " + fi1.toString() + " <> " + fi2.toString();
		assertThat(message, (int) Math.signum(comp.compare(fi1, fi2)), is(sign));
		assertThat(message, (int) Math.signum(comp.compare(fi2, fi1)), is(-sign));
	}

	@Test
	public void testCompare() {
		assertCompares(1, "a/file1.html", "a/setup.html");

		assertCompares(-1, "b/b.html", "d/b/a.html");
		assertCompares(-1, "d/setup.html", "d/a.html");
		assertCompares(1, "d/teardown.html", "d/setup.html");

		assertCompares(1, "d/teardown.html", "d/x.html");
		assertCompares(-1, "d/x.html", "d/teardown.html");
		assertCompares(-1, "d/setup.html", "d/a/setup.html");

		assertCompares(-1, "d/b/setUp.html", "d/z.html");
		assertCompares(-1, "x/setup.html", "x/y/setup.html");
		assertCompares(1, "x/teardown.html", "x/y/y.html");

		assertCompares(-1, "iteration_02/teardown.html",
				"iteration_03/test.html");
	}
}
