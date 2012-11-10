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


package ${groupId};

import java.io.File;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XMLShelfWriter implements ShelfWriter {
	private String filename = "bookshelf.xml";

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

	@Override
	public void write(final Bookshelf shelf) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document document = builder.newDocument();
		Element root = document.createElement("shelf");

		for (int i = 0; i < shelf.bookCount(); ++i) {
			Element book = document.createElement("book");
			book.setAttribute("id", Integer.toString(i));

			Element e = document.createElement("author");
			e.appendChild(document.createTextNode(shelf.get(i).getAuthor()));
			book.appendChild(e);

			e = document.createElement("title");
			e.appendChild(document.createTextNode(shelf.get(i).getTitle().toString()));
			book.appendChild(e);

			e = document.createElement("isbn");
			e.appendChild(document.createTextNode(shelf.get(i).getIsbn().stripped()));
			book.appendChild(e);

			e = document.createElement("price");
			e.appendChild(document.createTextNode(Float.toString(shelf.get(i).getPrice())));
			book.appendChild(e);

			root.appendChild(book);
		}

		document.appendChild(root);

		DOMSource source = new DOMSource(document);
		StreamResult output = new StreamResult(new File(filename));

		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.transform(source, output);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
