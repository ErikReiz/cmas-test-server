package com.cmasproject.cmastestserver.helpers;

public class TimeTranslator {
    public static int hoursToMilliseconds(int hours)
    {
        return hours * 60 * 60 * 1000;
    }
    public static long hoursToMilliseconds(long hours)
    {
        return hours * 60 * 60 * 1000;
    }
}
