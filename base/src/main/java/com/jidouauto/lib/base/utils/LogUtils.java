package com.jidouauto.lib.base.utils;

import android.util.Log;

import com.jidouauto.lib.base.BuildConfig;

/**
 * Created by simon on 18-2-3.
 */
public class LogUtils {

    private static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private static boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    private static boolean isPrintLog(String tag, int level) {
        return isDebug() || isLoggable(tag, level);
    }

    /**
     * Verbose日志仅仅只能在DEBUG环境里面打印
     */
    public static void v(String tag, String msg) {
        if (isDebug()) {
            Log.v(tag, checkMsg(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (isPrintLog(tag, Log.DEBUG)) {
            Log.d(tag, checkMsg(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (isPrintLog(tag, Log.INFO)) {
            Log.i(tag, checkMsg(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (isPrintLog(tag, Log.WARN)) {
            Log.w(tag, checkMsg(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (isPrintLog(tag, Log.ERROR)) {
            Log.e(tag, checkMsg(msg));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isPrintLog(tag, Log.ERROR)) {
            Log.e(tag, checkMsg(msg), tr);
        }
    }

    private static String checkMsg(String msg) {
        return msg == null ? "null" : msg;
    }
}
