package de.cologneintelligence.fitgoodies.util;

/**
 * Implementation of {@link SystemTime} which uses the real underlying system.
 *
 * @author kmussawisade
 * @version $Id$
 */
public final class SystemTimeImpl implements SystemTime {
    @Override
    public long currentSystemTimeInMS() {
        return System.currentTimeMillis();
    }

    @Override
    public void sleep(final long sleepTimeInMillis) {
        try {
            Thread.sleep(sleepTimeInMillis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
