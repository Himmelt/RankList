package org.soraworld.ranklist.util;

import java.util.Date;

/**
 * @author Himmelt
 */
public class DateUtils {

    public static long getDay(Date date, int timezone) {
        long timestamp = date.getTime() + 3600000 * timezone;
        return timestamp / 86400000L + 1;
    }

    public static long getDay(long timestamp, int timezone) {
        return (timestamp + 3600000 * timezone) / 86400000L + 1;
    }

    public static int getWeek(Date date, int timezone) {
        long days = getDay(date, timezone) + 3;
        return (int) ((days + 6) / 7);
    }

    public static int getWeekDay(Date date, int timezone) {
        long days = getDay(date, timezone) + 3;
        return (int) (days + 6) % 7 + 1;
    }

    public static long getDay(Date date) {
        long timestamp = date.getTime() + 28800000L;
        return timestamp / 86400000L + 1;
    }

    public static long getDay(long timestamp) {
        return (timestamp + 28800000L) / 86400000L + 1;
    }

    public static int getWeek(Date date) {
        long days = getDay(date) + 3;
        return (int) ((days + 6) / 7);
    }

    public static int getWeekDay(Date date) {
        long days = getDay(date) + 3;
        return (int) (days + 6) % 7 + 1;
    }
}
