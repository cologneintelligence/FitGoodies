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


package com.mycompany.bookstore;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public final class FileShelfWriter implements ShelfWriter {
	private String filename = "bookshelf.txt";

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String file) {
		this.filename = file;
	}

	@Override
	public Bookshelf load() {
		return null;
	}

	private String fixedWidth(final String s, final int width) {
		StringBuilder sb = new StringBuilder(width);

		sb.append(s);
		while (sb.length() < width) {
			sb.append(" ");
		}

		return sb.substring(0, width);
	}

	@Override
	public void write(final Bookshelf shelf) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			for (int i = 0; i < shelf.bookCount(); ++i) {
				Formatter format = new Formatter(Locale.US);
				Book b = shelf.get(i);
				writer.write(fixedWidth(Integer.toString(i), 5));
				writer.write(fixedWidth(b.getIsbn().stripped(), 15));
				writer.write(fixedWidth(b.getTitle().toString(), 80));
				writer.write(fixedWidth(b.getAuthor(), 50));
				format.format("%6.02f", b.getPrice());
				writer.write(fixedWidth(format.toString(), 9));
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
