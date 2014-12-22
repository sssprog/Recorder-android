package com.sssprog.recorder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public abstract class MediaController {

    protected final Activity activity;
    protected final MediaControllerListener listener;

    protected MediaController(Activity activity, MediaControllerListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public abstract void onActivityResume();
    public abstract void onActivityPause();

    protected void keepScreenOn() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void keepScreenOff() {
        activity.getWindow().setFlags(~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void saveState(Bundle state) {

    }

    public void restoreState(Bundle state) {

    }
}
