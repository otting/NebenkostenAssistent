package db.util;

import java.util.Date;

public class Time {
    public static int monthBetween(Date a, Date b) {
	return millisecondsToMonth(between(a, b));
    }

    public static int millisecondsToMonth(long time) {
	return (int) Math.round(millisecondsToDays(time) / 30.4);
    }

    public static long millisecondsToDays(long time) {
	return Math.round((time / (1000 * 60 * 60 * 24)));
    }

    /*
     * Returns absolute time between date a and b in milliseconds
     */
    public static long between(Date a, Date b) {
	long x = a.getTime() - b.getTime();
	return (x < 0) ? -x : x;

    }
}
