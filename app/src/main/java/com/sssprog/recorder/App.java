package com.sssprog.recorder;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.sssprog.recorder.utils.LogHelper;
import com.sssprog.recorder.utils.Prefs;
import com.sssprog.recorder.utils.RateTimeService;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    private static App instance;

    public App() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics.start(this);
        Prefs.init(this);
        RateTimeService.appLaunched();
        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf", R.attr.fontPath);
        Config.getAudioFile().getParentFile().mkdirs();

        try {
            Class.forName("android.os.AsyncTask"); //it prevents AdMob from crashing on HTC with Android 4.0.x
        } catch (Throwable ignored) {
            LogHelper.printStackTrace(ignored);
        }
    }

}
