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

package de.cologneintelligence.fitgoodies.external;

import java.io.*;
import java.util.Arrays;

public class SystemProcessWrapper implements ProcessWrapper {
	private File dir = new File(System.getProperty("user.dir"));

	@Override
	public void changeDir(String dir) {
		this.dir = new File(dir);
	}

	@Override
	public void start(final String command, final String... arguments) throws IOException {
		startProcess(command, arguments);
	}

	@Override
	public int startAndWait(String command, String... arguments) throws InterruptedException, IOException {
		Process process = startProcess(command, arguments);
		return process.waitFor();
	}

	private Process startProcess(final String command, final String... arguments) throws IOException {
		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.command().addAll(Arrays.asList(arguments));
		builder.directory(dir);
		builder.redirectErrorStream(true);
		final Process process = builder.start();

		copyStreamAsync(process.getInputStream(), System.out);
		copyStreamAsync(process.getErrorStream(), System.err);

		return process;
	}

	private void copyStreamAsync(final InputStream is, final PrintStream output) {
		new Thread() {
			@Override
			public void run() {
				try {
					final InputStreamReader isr = new InputStreamReader(is);
					final BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						output.println(line);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}.run();
	}
}
