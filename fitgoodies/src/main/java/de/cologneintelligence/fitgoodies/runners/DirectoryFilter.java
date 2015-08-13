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
import de.cologneintelligence.fitgoodies.file.SimpleRegexFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DirectoryFilter {
    private final List<FileInformation> allFiles = new ArrayList<>();
    private SimpleRegexFilter filter = new SimpleRegexFilter("(?i).*\\.html?");
    private boolean limited;
    private List<FileInformation> limits = new ArrayList<>();
    private FileSystemDirectoryHelper fsHelper;

    public DirectoryFilter(File dir, FileSystemDirectoryHelper fsHelper) {
        this.fsHelper = fsHelper;
        listFiles(dir);
        sort(allFiles);
    }

    private void listFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file);
                } else if (filter.accept(file)) {
                    allFiles.add(new FileInformation(file.getAbsoluteFile()));
                }
            }
        }
    }

    public List<FileInformation> getSelectedFiles() {
        List<FileInformation> result;
        if (limited) {
            result = new LinkedList<>();
            filterList(result);
        } else {
            result = allFiles;
        }

        return result;
    }

    private void sort(List<FileInformation> result) {
        Collections.sort(result, new FitFileInformationComparator());
    }

    private void filterList(List<FileInformation> result) {
        int pos = 0;

        sort(limits);

        for (FileInformation file : allFiles) {
            if (pos < limits.size() && file.equals(limits.get(pos))) {
                result.add(file);
                pos++;
            } else if(pos > 0 && isRelevantAs(file, limits.get(pos - 1), "teardown.html")) {
                result.add(file);
            } else if(pos < limits.size() && isRelevantAs(file, limits.get(pos), "setup.html")) {
                result.add(file);
            }
        }
    }

    private boolean isRelevantAs(FileInformation file, FileInformation forFile, String name) {
        if (!file.filename().equals(name)) {
            return false;
        }

        try {
            File possiblySubDir = forFile.getFile().getParentFile();
            File parentDir = file.getFile().getParentFile();
            return fsHelper.isSubDir(possiblySubDir, parentDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLimit(File file) {
        limited = true;
        limits.add(new FileInformation(file.getAbsoluteFile()));
    }
}
