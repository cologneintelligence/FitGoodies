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

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This fixture runs external commands and optionally waits for them to finish
 * <p/>
 * <p/>
 * <table border="1" summary="">
 * <tr>
 * <td>fitgoodies.external.ProcessWrapper</td>
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
 */
// TODO: better extend Fixture
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
        FitCell resultCell = row.cells().get(1);

        if (result == 0) {
			resultCell.right();
		} else {
            resultCell.wrong("Return code: " + result);
		}
	}

	private String[] getParameters() {
		List<String> parameterList = new LinkedList<>();

        for (int i = 3; i < row.cells().size(); i++) {
            String fitValue = row.cells().get(i).getFitValue();
            parameterList.add(validator.preProcess(fitValue));
        }

		SetupHelper setupHelper = DependencyManager.getOrCreate(SetupHelper.class);
		parameterList.addAll(setupHelper.getProperties());
        return parameterList.toArray(new String[parameterList.size()]);
	}

	public void changeDir(String dir) {
		processWrapper.changeDir(dir);
	}
}
