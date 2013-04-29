package de.cologneintelligence.fitgoodies.external;

import java.io.File;
import java.io.IOException;
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
        return builder.start();
    }
}
