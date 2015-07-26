package com.mibcxb.android.util;

import com.mibcxb.arduino.dfrobot.bluno.BuildConfig;

import android.util.Log;

public class McLog {
    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;

    private static int sLevel = BuildConfig.DEBUG ? DEBUG : WARN;

    private McLog() {
    }

    public static int getLevel() {
        return sLevel;
    }

    public static void setLevel(int level) {
        McLog.sLevel = Math.min(Math.max(level, VERBOSE), ERROR);
    }

    public static void v(String tag, String msg) {
        if (sLevel <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sLevel <= VERBOSE) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (sLevel <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sLevel <= DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sLevel <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sLevel <= INFO) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sLevel <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sLevel <= WARN) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (sLevel <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sLevel <= ERROR) {
            Log.e(tag, msg, tr);
        }
    }

    public static String getSimpleTag(Class<?> cls) {
        if (cls == null) {
            return "mcdroid";
        } else {
            return cls.getSimpleName();
        }
    }

}
