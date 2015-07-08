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

package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ArgumentParser {
    private static final String DEFAULT_ENCODING = "utf-8";

    private final File pwd;
    private File destinationDir;
    private String encoding;
    private List<FileInformation> files = new LinkedList<FileInformation>();
    private FileSystemDirectoryHelper fsHelper;
    private DirectoryFilter currentDirFilter;
    private File sourceDir;

    public ArgumentParser(File pwd, FileSystemDirectoryHelper fsHelper) {
        this.pwd = pwd;
        this.fsHelper = fsHelper;
    }

    public void parse(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            String arg = args[i];

            if ("-d".equals(arg) || "--destination".equals(arg)) {
                this.destinationDir = new File(getOption(args, i, "-d", this.destinationDir));
            } else if ("-e".equals(arg) || "--encoding".equals(arg)) {
                this.encoding = getOption(args, i, "-e", this.encoding);
            } else if ("-f".equals(arg) || "--file".equals(arg)) {
                finishDir();
                addFile(getOption(args, i, "-f", null));
            } else if ("-s".equals(arg) || "--source".equals(arg)) {
                finishDir();
                addDirectory(getOption(args, i, "s", sourceDir));
            } else if ("-o".equals(arg) || "--only".equals(arg)) {
                if (currentDirFilter == null) {
                    throw new IllegalArgumentException("Limit must follow -s or -o");
                }
                addLimit(getOption(args, i, "l", null));
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }

        if (destinationDir == null) {
            throw new IllegalArgumentException("Option -d is required");
        }

        finishDir();
    }

    private void addLimit(String limit) {
        File absFile = fsHelper.rel2abs(sourceDir.getAbsolutePath(), limit);
        currentDirFilter.addLimit(absFile);
    }

    private void addFile(String file) {
        files.add(new FileInformation(new File(file).getAbsoluteFile()));
    }

    private void addDirectory(String file) {
        finishDir();
        sourceDir = fsHelper.rel2abs(pwd.getAbsolutePath(), file);
        currentDirFilter = new DirectoryFilter(sourceDir, fsHelper);
    }

    private void finishDir() {
        if (currentDirFilter != null) {
            for (FileInformation fileInformation : currentDirFilter.getSelectedFiles()) {
                files.add(fileInformation);
            }
            currentDirFilter = null;
        }
    }

    private String getOption(String[] args, int i, String option, Object oldValue) {
        if (i == args.length - 1) {
            throw new IllegalArgumentException("Option " + option + " is missing an argument!");
        }

        if (oldValue != null) {
            throw new IllegalArgumentException("Option " + option + " is set twice!");
        }

        return args[i + 1];
    }

    public File getDestinationDir() {
        return destinationDir;
    }

    public String getEncoding() {
        return encoding;
    }

    public List<FileInformation> getFiles() {
        return files;
    }

    public File getBaseDir() {
        return sourceDir;
    }
}
