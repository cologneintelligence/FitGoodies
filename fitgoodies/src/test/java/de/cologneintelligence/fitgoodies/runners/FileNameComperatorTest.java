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

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileInformationMock;
import de.cologneintelligence.fitgoodies.runners.FileNameComperator;

/**
 * $Id$
 * @author jwierum
 */
public class FileNameComperatorTest extends FitGoodiesTestCase {
	private final FileNameComperator comp = new FileNameComperator();

	private void assertCompares(final int sign,
			final String p1, final String f1,
			final String p2, final String f2) {

		FileInformation fi1 = new FileInformationMock(p1, f1, null);
		FileInformation fi2 = new FileInformationMock(p2, f2, null);

		super.assertCompares(fi1, fi2, comp, sign);
	}

	public final void testCompare() {
		assertCompares(-1, "b/", "b.html", "d/b", "a.html");
		assertCompares(-1, "d/", "setup.html", "d/", "a.html");
		assertCompares(1, "d/", "teardown.html", "d/", "setup.html");

		assertCompares(1, "d/", "teardown.html", "d/", "x.html");
		assertCompares(-1, "d/", "x.html", "d/", "teardown.html");
		assertCompares(-1, "d/", "setup.html", "d/a/", "setup.html");

		assertCompares(-1, "d/b/", "setUp.html", "d/", "z.html");
		assertCompares(-1, "x/", "setup.html", "x/y/", "setup.html");
		assertCompares(1, "x/", "teardown.html", "x/y/", "y.html");

		assertCompares(-1, "iteration_02/", "teardown.html",
				"iteration_03/", "test.html");
	}
}
