package com.xyz.awesomerating.internal;

import android.util.Log;

public final class RatingLogger {
    private static final String TAG = "awesome_app_rating";
    private static boolean enabled = true;

    private RatingLogger() {}

    public static void setEnabled(boolean value) { enabled = value; }

    public static void verbose(String msg) { if (enabled) Log.v(TAG, msg); }
    public static void debug(String msg)   { if (enabled) Log.d(TAG, msg); }
    public static void info(String msg)    { if (enabled) Log.i(TAG, msg); }
    public static void warn(String msg)    { if (enabled) Log.w(TAG, msg); }
    public static void error(String msg)   { if (enabled) Log.e(TAG, msg); }
    public static void error(String msg, Throwable t) { if (enabled) Log.e(TAG, msg, t); }
}
