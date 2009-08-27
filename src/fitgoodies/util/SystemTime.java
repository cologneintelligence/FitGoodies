package fitgoodies.util;

/**
 * Interface which provides time functions.
 *
 * @author kmussawisade
 * @version $Id$
 */
public interface SystemTime {
	/**
	 * Returns the current system time in milliseconds.
	 * @return the current system time
	 */
    long currentSystemTimeInMS();

    /**
     * Stops the thread for <code>sleepTimeInMillis</code> milliseconds.
     * @param sleepTimeInMillis length of sleep period
     */
    void sleep(long sleepTimeInMillis);
}
