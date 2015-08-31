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

package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This Fixture can be used to test the content of a XML file using XPath-Expressions.
 * <p>
 * Example:
 * <table border="1" summary="">
 * <tr><td>fitgoodies.file.XMLFileFixture</td><td>file=/myfile.xml</td></tr>
 * <tr><td>/books/book[0]/author</td><td>Terry Pratchett</td></tr>
 * <tr><td>/books/book[1]/id</td><td>326172</td></tr>
 * </table>
 */
public class XMLFileFixture extends AbstractFileReaderFixture {
    private Document doc;
    private XPathFactory xPathFactory;

    public XMLFileFixture() {
        super();
    }

    XMLFileFixture(FileInformationWrapper wrapper) {
        super(wrapper);
    }

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
    // FIXME: process cell parameter
    protected void doRow(FitRow row) {
        String xpath = row.cells().get(0).getFitValue();
        XPath path = xPathFactory.newXPath();

        try {
            String selectedValue = path.evaluate(xpath, doc);
            ConstantReceiver receiver = new ConstantReceiver(selectedValue);
            check(row.cells().get(1), receiver, null);
        } catch (XPathExpressionException | SecurityException e) {
            row.exception(e);
        }
    }
}
