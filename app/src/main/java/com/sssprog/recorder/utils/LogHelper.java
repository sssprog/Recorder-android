package com.sssprog.recorder.utils;

import android.util.Log;

import com.sssprog.recorder.Config;


public class LogHelper {

    public static void i(String tag, String message) {
        if (Config.DEBUG) Log.i(tag, message);
    }

    public static void d(String tag, String message) {
        if (Config.DEBUG) Log.d(tag, message);
    }

    public static void w(String tag, String message) {
        if (Config.DEBUG) Log.w(tag, message);
    }

    public static void e(String tag, String message) {
        if (Config.DEBUG) Log.e(tag, message);
    }

    public static String getTag(Class<?> clazz) {
        return "RAP::" + clazz.getSimpleName();
    }

    public static void printStackTrace(Throwable e) {
        if (Config.DEBUG) e.printStackTrace();
    }

}
