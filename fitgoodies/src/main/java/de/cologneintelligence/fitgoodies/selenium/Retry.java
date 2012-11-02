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
