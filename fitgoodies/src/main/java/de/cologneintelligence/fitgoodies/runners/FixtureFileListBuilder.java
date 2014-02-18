package de.cologneintelligence.fitgoodies.runners;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class FixtureFileListBuilder {
    private String currentDir;
    private final String separator;
    private final String baseDir;
    private final List<String> files;

    public FixtureFileListBuilder(final String baseDir){
        this.separator = File.separator;
        files = new ArrayList<String>();
        this.baseDir = new File(baseDir).getPath();
        currentDir = baseDir;
        addIfExists(new File(baseDir), "setup.html");

    }

    /**
     * Adds the selected files and all corresponding setup and teardown files to a list and then calls {@link #returnFiles()} to return the list.
     * @param file	- The filename received from the runner
     */
    public void addFile(final String file){
        final String[] baseDirArray = currentDir.split(Pattern.quote(separator), -1);
        final String parent = new File(baseDir, file).getParent();
        final String[] pathArray = parent.split(Pattern.quote(separator));
        final int indexOfPrefix = getCommonIndex(baseDirArray, pathArray);

        for (int i = baseDirArray.length - 1; i >= indexOfPrefix; i--) {
            final File fullFilePath = createDirectoryToFile(indexOfPrefix,
                    baseDirArray, i);
            addIfExists(fullFilePath, "teardown.html");
        }

        for (int i = indexOfPrefix; i < pathArray.length; i++) {
            final File fullFilePath = createDirectoryToFile(indexOfPrefix,
                    pathArray, i);
            addIfExists(fullFilePath, "setup.html");
        }

        final File fullFilePath = new File(baseDir);
        addIfExists(fullFilePath, file);
        currentDir = parent;
    }

    /**
     * @param fullFilePath	- Path where to add file
     * @param fileName		- filename
     */
    private void addIfExists(final File fullFilePath, final String fileName) {
        final File file = new File(fullFilePath, fileName);
        System.out.println("aie: " + file);
        if (file.exists()) {
            final String diff = StringUtils.difference(baseDir, fullFilePath.getPath());
            files.add(diff + fileName);
        }
    }

    /**
     * Before returning the list it needs to iterate from current directory to the base directory and add the missing tear down files.
     * @return returns a list with all the file names, including the relevant setup and teardown files.
     */
    public List<String> returnFiles(){
        addFile("teardown.html");
        return files;
    }

    /**
     * @param baseDirArray	- the path to the base directory, split by File.separator
     * @param pathArray		- the path to the selected file, split by File.separator
     * @param indexOfPrefix	- the position of the suffix of the comparison between base and path directory.
     * @return
     */
    private int getCommonIndex(final String[] baseDirArray,
            final String[] pathArray) {
        int indexOfPrefix = 0;
        for(;indexOfPrefix<Math.min(pathArray.length, baseDirArray.length); indexOfPrefix++){
            if (!pathArray[indexOfPrefix].equals(baseDirArray[indexOfPrefix])){
                break;
            }
        }
        return indexOfPrefix;
    }

    /**
     * @param start
     * @param dirArray
     *            - the path to the base directory, split by File.separator
     * @param pathIndex
     *            - index of current directory iteration
     * @return a file containing the full path
     */
    private File createDirectoryToFile(final int start,
            final String[] dirArray, final int pathIndex) {
        File f = new File(baseDir);
        for (int i = start; i <= pathIndex; i++) {
            f = new File(f, dirArray[i]);
        }
        return f;
    }
}
