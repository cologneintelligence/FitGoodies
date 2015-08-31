package de.cologneintelligence.fitgoodies.htmlparser;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class FitDocumentTest {
	@Test
	public void stringIsParsedAsDocument() throws IOException {
		FitDocument document = load("notables.html");
		assertThat(document, is(notNullValue()));
		assertThat(document.tables().size(), is(0));
		assertDocumentContent(document, "notables-result.html");
	}

	@Test
	public void tablesAreFound() throws IOException {
		FitDocument document = load("tables.html");
		assertThat(document.tables().size(), is(2));
		assertDocumentContent(document, "tables-result.html");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void tablesAreUnmodifiable() throws IOException {
		load("tables.html").tables().add(null);
	}

	@Test
	public void tablesAreExposed() throws IOException {
		FitDocument document = load("tables.html");
		document.tables().get(0).getTable().attr("test", "success");
		assertDocumentContent(document, "tables-manipulated-result.html");
	}

	@Test
	public void brokenTablesAreSkipped() throws IOException {
		FitDocument document = load("broken.html");
		assertThat(document.tables().size(), is(0));
		assertDocumentContent(document, "broken-result.html");
	}

	@Test
	public void tablesAreIgnored() throws IOException {
		FitDocument document = load("tables-ignore.html");
		assertThat(document.tables().size(), is(1));
		assertDocumentContent(document, "tables-ignore-result.html");
	}

	private FitDocument load(String name) throws IOException {
		try (InputStream is = FitDocumentTest.class.getResourceAsStream(name)) {
			return FitDocument.parse(is, "UTF-8");
		}
	}

	private void assertDocumentContent(FitDocument document, String resultFile) throws IOException {
		assertThat(clean(document.getHtml()), equalTo(loadFile(resultFile)));
	}

	private String loadFile(String name) throws IOException {
		try (InputStream is = FitDocumentTest.class.getResourceAsStream(name)) {
			return clean(IOUtils.toString(is));
		}
	}

	private String clean(String html) {
		return html.replaceAll("[\n\r\t ]+", " ").trim();
	}
}
