package de.cologneintelligence.fitgoodies.runners;

import java.io.File;

public class FitFileComparator extends FitFilenameComparator<File> {
    @Override
    protected File getFile(File file) {
        return file;
    }

    @Override
    protected String getFileName(File file) {
        return file.getName();
    }
}
