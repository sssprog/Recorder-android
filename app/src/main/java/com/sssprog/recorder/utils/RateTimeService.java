package com.sssprog.recorder.utils;

import android.text.format.DateUtils;

import com.sssprog.recorder.R;


public class RateTimeService {

    private static final int MIN_USAGE_COUNT = 25;
    private static final long MIN_TIME_SINCE_APP_INSTALL = 3 * DateUtils.DAY_IN_MILLIS;

    public static void incrementUsageCounter() {
        if (Prefs.getBoolean(R.string.prefs_do_not_show_rare_suggestion)) {
            return;
        }
        int count = Prefs.getInt(R.string.prefs_usage_counter);
        Prefs.putInt(R.string.prefs_usage_counter, count + 1);
    }

    public static void appLaunched() {
        if (!Prefs.contains(R.string.prefs_app_install_time)) {
            Prefs.putLong(R.string.prefs_app_install_time, System.currentTimeMillis());
        }
    }

    public static boolean canShowSuggestion() {
        if (Prefs.getBoolean(R.string.prefs_do_not_show_rare_suggestion)) {
            return false;
        }
        long timeSinceInstall = System.currentTimeMillis() - Prefs.getLong(R.string.prefs_app_install_time);
        int count = Prefs.getInt(R.string.prefs_usage_counter);
        return count >= MIN_USAGE_COUNT && timeSinceInstall >= MIN_TIME_SINCE_APP_INSTALL;
    }

    public static void doNotShowAnymore() {
        Prefs.putBoolean(R.string.prefs_do_not_show_rare_suggestion, true);
    }

}
