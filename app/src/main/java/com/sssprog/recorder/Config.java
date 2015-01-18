package com.sssprog.recorder;

import com.google.android.gms.ads.AdRequest;

import java.io.File;

public class Config {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean SHOW_ADDS_IN_DEBUG = true;

    public static String getAudioFilePath() {
        return App.getInstance().getFilesDir() + "/audio.gpp";
    }

    public static File getAudioFile() {
        return new File(getAudioFilePath());
    }

    public static boolean showAds() {
        return !DEBUG || SHOW_ADDS_IN_DEBUG;
    }

    public static void addTestDevices(AdRequest.Builder adRequestBuilder) {
        if (DEBUG) {
            adRequestBuilder.addTestDevice("A93C83B2EF8903900744E90388854F54"); // Nexus4
            adRequestBuilder.addTestDevice("62B1850747D18A624F082E6CF653F3A9"); // Nexus7
            adRequestBuilder.addTestDevice("CDF8B37A72A8C052FEE4764E26D17B9D"); // Nexus5
        }
    }

}
