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


package com.mycompany.bookstore;

public final class Book {
	private StringBuffer title;
	private String author;
	private ISBN isbn;
	private float price;

	public Book(final StringBuffer bookTitle, final String bookAuthor,
	            final ISBN bookIsbn, final float bookPrice) {
		this.title = bookTitle;
		this.author = bookAuthor;
		this.isbn = bookIsbn;
		this.price = bookPrice;
	}

	public StringBuffer getTitle() {
		return title;
	}

	public void setTitle(final StringBuffer bookTitle) {
		this.title = bookTitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String bookAuthor) {
		this.author = bookAuthor;
	}

	public ISBN getIsbn() {
		return isbn;
	}

	public void setIsbn(final ISBN bookIsbn) {
		this.isbn = bookIsbn;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(final float bookPrice) {
		this.price = bookPrice;
	}

	public boolean isValid() {
		return (isbn.isValid() && !empty(author)
				&& !empty(title.toString()) && price >= 0.0f);
	}

	private boolean empty(final String s) {
		return s == null || s.trim().length() == 0;
	}
}
