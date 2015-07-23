package fat;

import de.cologneintelligence.fitgoodies.ColumnFixture;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.io.*;

public class TextToHtmlFixture extends ColumnFixture {
	public String Text;

	public String HTML() {
		Text = unescapeAscii(Text);
		return FitUtils.escape(Text);
	}

	private String unescapeAscii(String text) {
		text = text.replaceAll("\\\\n", "\n");
		text = text.replaceAll("\\\\r", "\r");
		return text;
	}

	private String GenerateOutput(Parse parse) {
		StringWriter result = new StringWriter();
		parse.print(new PrintWriter(result));
		return result.toString();
	}
}
