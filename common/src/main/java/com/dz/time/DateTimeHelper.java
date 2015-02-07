package com.dz.time;

/**
 * Helper class for working with date/time
 * @author mamad
 * @since 04/12/14.
 */
public final class DateTimeHelper {
    /**
     * Why another method for <code>System.currentTimeMillis()</code>?
     * Because:
     *  1) We need to get time in UTC, not local time
     *  2) We could use external servers to sync time rather relaying on server time.
     *
     *  Currently just returning System.currentTimeMillis() value.
     * @return current time value, later in UTC format
     */
    public static long currentTimeInMs() {
        return System.currentTimeMillis();
    }
}