package de.cologneintelligence.fitgoodies.external;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

/**
 * This fixture runs external commands and optionally waits for them to finish<br />
 * <br />
 *
 * <table border="1">
 * <tr>
 * <td colspan="3">fitgoodies.external.ProcessWrapper</td>
 * </tr>
 * <tr>
 * <td>execute</td>
 * <td>c:\windows\system32\notepad.exe</td>
 * <td>c:\myfile.txt</td>
 * </tr>
 * <tr>
 * <td>changeDir</td>
 * <td colspan="2">c:\</td>
 * </tr>
 * <tr>
 * <td>executeAndWait</td>
 * <td>c:\windows\system32\notepad.exe</td>
 * <td>c:\myfile.txt</td>
 * </tr>
 * </table>
 *
 */
public class StartFixture extends ActionFixture {

    private ProcessWrapper processWrapper;

    public StartFixture() {
        this(new SystemProcessWrapper());
    }

    public StartFixture(ProcessWrapper processWrapper) {
        this.processWrapper = processWrapper;
    }

    public void execute() throws Exception {
        transformAndEnter();
    }

    public void executeAndWait() throws Exception {
        transformAndEnter();
    }

    public void changeDir() throws Exception {
        transformAndEnter();
    }

    public void execute(String command) throws IOException {
        processWrapper.start(command, getParameters());
    }


    public void executeAndWait(String command) throws Exception {
        int result = processWrapper.startAndWait(command, getParameters());
        if (result == 0) {
            right(cells);
        } else {
            wrong(cells, "Return code: " + result);
        }
    }

    private String[] getParameters() {
        List<String> parameterList = new LinkedList<String>();
        Parse cell = cells.more.more.more;
        while (cell != null) {
            parameterList.add(cell.text());
            cell = cell.more;
        }

        SetupHelper setupHelper = DependencyManager.getOrCreate(SetupHelper.class);
        parameterList.addAll(setupHelper.getProperties());
        final String[] parameters = parameterList.toArray(new String[parameterList.size()]);
        return parameters;
    }

    public void changeDir(String dir) {
        processWrapper.changeDir(dir);
    }
}
