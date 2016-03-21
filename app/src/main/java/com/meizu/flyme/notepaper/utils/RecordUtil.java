package com.meizu.flyme.notepaper.utils;

public class RecordUtil {
    public static String timeConvert(long seconds) {
        int s = ((int) seconds) % 60;
        int m = (int) ((seconds / 60) % 60);
        if (((int) seconds) / 3600 > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(((int) seconds) / 3600), Integer.valueOf(m), Integer.valueOf(s)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(m), Integer.valueOf(s)});
    }
}
