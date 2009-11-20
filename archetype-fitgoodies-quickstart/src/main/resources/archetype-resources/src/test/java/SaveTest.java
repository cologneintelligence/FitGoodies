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

package ${groupId};

import fitgoodies.Fixture;

public final class SaveTest extends Fixture {
	private boolean written;

    public void savedb() {
    	written = false;
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ShelfWriter writer;
				writer = new DerbyShelfWriter();
				writer.write(FixtureObjects.SHELF);

				FileShelfWriter filewriter = new FileShelfWriter();
				filewriter.setFilename("demo-output/bookshelf.txt");
				filewriter.write(FixtureObjects.SHELF);

				XMLShelfWriter xmlwriter = new XMLShelfWriter();
				xmlwriter.setFilename("demo-output/bookshelf.xml");
				xmlwriter.write(FixtureObjects.SHELF);

				written = true;
			}
    	});
    	t.start();
    }

    public boolean isSaved() {
    	return written;
    }
}
