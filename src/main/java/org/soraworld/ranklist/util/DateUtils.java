package org.soraworld.ranklist.util;

import java.util.Date;

/**
 * @author Himmelt
 */
public class DateUtils {
    public static long getDay(Date date) {
        long timestamp = date.getTime() + 28800000L;
        return timestamp / 86400000L + 1;
    }

    public static long getDay(long timestamp) {
        return (timestamp + 28800000L) / 86400000L + 1;
    }

    public static long getWeek(Date date) {
        long days = getDay(date) + 3;
        return (days + 6) / 7;
    }

    public static int getWeekDay(Date date) {
        long days = getDay(date) + 3;
        return (int) (days + 6) % 7 + 1;
    }
}
