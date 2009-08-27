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


package fitgoodies.file.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader which is capable to process comma separated value files.
 *
 * @author jwierum
 * @version $Id$
 */
public class CSVRecordReader implements FileRecordReader {
	private static class ParserState {
		private boolean masked = false;
		private boolean maskNext = false;
		private boolean skip = false;

		public boolean masked() { return masked; }
		public boolean notMasked() { return !masked(); }
		public void mask(final boolean doIt) { this.masked = doIt; }

		public boolean nextMasked() { return maskNext; }
		public boolean nextNotMasked() { return !nextMasked(); }
		public void maskNext(final boolean doIt) { this.maskNext = doIt; }

		public boolean skipped() { return skip; }
		public boolean notSkipped() { return !skipped(); }
		public void skip(final boolean doIt) { skip = doIt; }
	}

	private final BufferedReader reader;
	private final char delimiterChar;
	private final char maskChar;
	private String[] parts;
	private int partIndex;

	/**
	 * Creates a new reader object.
	 * @param bufferedReader underlying stream to process
	 * @param fieldDelimiter field delimiter character (usually a comma)
	 * @param fieldMask masking character (usually a quotation mark)
	 * @throws IOException thrown if <code>bufferedReader</code> reports a problem
	 */
	public CSVRecordReader(
			final BufferedReader bufferedReader,
			final char fieldDelimiter,
			final char fieldMask) throws IOException {

		reader = bufferedReader;
		delimiterChar = fieldDelimiter;
		maskChar = fieldMask;

		nextRecord();
	}

	/**
	 * Closes the underlying stream.
	 */
	@Override
	public final void close() throws IOException {
		reader.close();
	}

	/**
	 * Returns the next field in the record set.
	 * @return next field's value, or <code>null</code>, if the last column has
	 * 		been reached.
	 */
	@Override
	public final String nextField() {
		if (parts == null || partIndex >= parts.length) {
			return null;
		}

		String returnValue = parts[partIndex];
		++partIndex;
		return returnValue;
	}

	/**
	 * Reads the next row.
	 * @return <code>true</code> if a record set could been read,
	 * 		<code>false</code> on end of file.
	 * @throws IOException when the underlying Stream reports an error
	 */
	@Override
	public final boolean nextRecord() throws IOException {
		String line = reader.readLine();
		if (line == null) {
			parts = null;
			return false;
		}

		List<String> newParts = splitLine(line);
		parts = newParts.toArray(new String[]{});
		partIndex = 0;
		return true;
	}

	private List<String> splitLine(final String line) {
		List<String> newParts = new ArrayList<String>();
		ParserState state = new ParserState();

		StringBuilder todo = new StringBuilder(line);
		StringBuilder builder = new StringBuilder();

		while (todo.length() > 0) {
			char character = todo.charAt(0);
			todo.deleteCharAt(0);

			changeState(state, character);
			String result = processChar(state, builder, character);
			if (result != null) {
				newParts.add(result);
			}
		}

		if (builder.length() > 0) {
			newParts.add(builder.toString().trim());
		}

		return newParts;
	}

	private String processChar(final ParserState state,
			final StringBuilder builder, final char character) {
		String result = null;
		if (character == delimiterChar && state.notMasked() && state.notSkipped()) {
			result = builder.toString().trim();
			builder.delete(0, builder.length());
		} else if (state.notSkipped()) {
			builder.append(character);
		}
		return result;
	}

	private void changeState(final ParserState state, final char character) {
		state.skip(false);
		if (character == maskChar && state.nextNotMasked()) {
			state.skip(true);
			state.maskNext(true);
		} else if (character == maskChar && state.nextMasked()) {
			state.maskNext(false);
		} else if (state.nextMasked()) {
			state.mask(state.notMasked());
			state.maskNext(false);
		}
	}

	/**
	 * Reports whether {@link #nextField()} can be called or whether no more
	 * record set is available.
	 * @return <code>true</code> if {@link #nextField()} will return values,
	 * 		<code>false</code> otherwise
	 */
	@Override
	public final boolean canRead() {
		return parts != null;
	}
}
