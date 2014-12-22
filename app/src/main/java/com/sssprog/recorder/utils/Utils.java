package com.sssprog.recorder.utils;

import java.util.Locale;

public class Utils {

    public static String formatPlayTime(long time) {
        time /= 1000;
        long seconds = time % 60;
        time /= 60;
        long minutes = time % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}
