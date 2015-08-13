/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

package de.cologneintelligence;

import org.apache.maven.plugin.logging.Log;

import java.io.*;

class StreamLogger extends Thread {
    private final InputStream is;
    private final boolean warn;
    private final Log log;

    public StreamLogger(InputStream is, boolean warn, Log log) {
        this.is = is;
        this.warn = warn;
        this.log = log;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String message = "[FIT] " + line;

                if (warn) {
                    log.warn(message);
                } else {
                    log.info(message);
                }
            }
        } catch (IOException e) {
            log.error("Could not read stream", e);
        }
    }
}
