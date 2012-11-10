/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.Wait;

public abstract class Retry extends Wait {
    private boolean result;
    private int counter;
    private final Long timeoutInMilliseconds;
    private final Long intervalInMilliseconds;

    public int attemptCounter() {
        return counter;
    }

    public Retry(final Long timeout, final Long interval) {
        this.timeoutInMilliseconds = timeout;
        this.intervalInMilliseconds = interval;
    }

    @Override
    public boolean until() {
        try {
            counter++;
            result = execute();
        } catch (RetryException e) {
            result = false;
        }
        return result;
    }

    public boolean start() {
        try {
            wait("TimeoutError!", timeoutInMilliseconds, intervalInMilliseconds);
            return true;
        } catch (Wait.WaitTimedOutException e) {
            return false;
        }
    }

    public abstract boolean execute();

}
