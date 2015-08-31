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

package de.cologneintelligence.fitgoodies.selenium.command;

import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class CaptureEntirePageScreenshotCommand extends WrappedCommand {
	private final long sleepBeforeScreenshot;

	public CaptureEntirePageScreenshotCommand(final String command, final String[] args,
	                                          final SetupHelper helper) {
		super(command, args, helper.getCommandProcessor());
		this.sleepBeforeScreenshot = helper.getSleepBeforeScreenshotMillis();
	}

	@Override
	public String execute() {
		waitBeforeTakingScreenshot();
		return commandProcessor.doCommand("captureEntirePageScreenshot", args);
	}

	private void waitBeforeTakingScreenshot() {
		try {
			Thread.sleep(sleepBeforeScreenshot);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
