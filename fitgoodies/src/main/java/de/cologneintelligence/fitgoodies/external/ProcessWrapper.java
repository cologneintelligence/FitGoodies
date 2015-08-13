package de.cologneintelligence.fitgoodies.external;

import java.io.IOException;

public interface ProcessWrapper {
    void start(String command, String... arguments) throws IOException;

    int startAndWait(String command, String... arguments) throws IOException, InterruptedException;

    void changeDir(String dir);
}
