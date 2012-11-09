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


package de.cologneintelligence.fitgoodies.file;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import fit.Parse;
import fit.TypeAdapter;

/**
 * This Fixture can be used to test the content of a XML file using XPath-Expressions.
 * <br /><br />
 *
 * Example:<br />
 * <table>
 * 		<tr><td>fitgoodies.file.XMLFileFixture</td><td>file=/myfile.xml</td></tr>
 * 		<tr><td>/books/book[0]/author</td><td>Terry Pratchett</td></tr>
 * 		<tr><td>/books/book[1]/id</td><td>326172</td></tr>
 * </table>
 *
 * @author jwierum
 * @version $Id$
 */
public class XMLFileFixture extends AbstractFileReaderFixture {
	/** for internal use only - used to solve cross references. */
	public String selectedValue;

	private Document doc;
	private XPathFactory xPathFactory;

	// http://www.ibm.com/developerworks/library/x-javaxpathapi.html
	@Override
	public void setUp() throws Exception {
		super.setUp();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(getFile().openInputStream());

		xPathFactory = XPathFactory.newInstance();
	}

	@Override
	public void doRow(final Parse row) {
		if (row.parts.more != null) {
			String xpath = row.parts.text();
			XPath path = xPathFactory.newXPath();

			try {
				selectedValue = path.evaluate(xpath, doc);
				check(row.parts.more, TypeAdapter.on(this,
						this.getClass().getField("selectedValue")));
			} catch (XPathExpressionException e) {
				exception(row.parts, e);
			} catch (SecurityException e) {
				exception(row.parts, e);
			} catch (NoSuchFieldException e) {
				exception(row.parts, e);
			}
		}
	}
}
