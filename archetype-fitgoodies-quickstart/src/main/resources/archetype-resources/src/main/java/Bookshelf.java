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


package ${package};

import java.util.ArrayList;

public final class Bookshelf {
	private final ArrayList<Book> books = new ArrayList<Book>();

	public Integer addBook(final Book b) {
		if (b.isValid()) {
			for (Book book : books) {
				if (book.getIsbn().equals(b.getIsbn())) {
					return null;
				}
			}

			books.add(b);
			return books.size() - 1;
		} else {
			return null;
		}
	}

	public Book get(final Integer lookup) {
		return books.get(lookup);
	}

	public int bookCount() {
		return books.size();
	}
}
