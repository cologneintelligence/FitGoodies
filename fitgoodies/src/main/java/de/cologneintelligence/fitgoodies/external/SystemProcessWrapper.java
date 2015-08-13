package de.cologneintelligence.fitgoodies.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
                    while ( (line = br.readLine()) != null) {
                        output.println(line);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.run();
    }
}
