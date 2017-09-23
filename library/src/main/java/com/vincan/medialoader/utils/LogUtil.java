package com.vincan.medialoader.utils;

import android.util.Log;

import com.vincan.medialoader.MediaLoader;

/**
 * log工具类
 */
public final class LogUtil {

    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static volatile boolean writeDebugLogs = false;
    private static volatile boolean writeLogs = true;

    private LogUtil() {
    }

    /**
     * 进行打开或者关闭调试日志打印功能
     */
    public static void writeDebugLogs(boolean writeDebugLogs) {
        LogUtil.writeDebugLogs = writeDebugLogs;
    }

    /**
     * 打开或者关闭所有日志打印功能
     */
    public static void writeLogs(boolean writeLogs) {
        LogUtil.writeLogs = writeLogs;
    }

    public static void d(String message, Object... args) {
        if (writeDebugLogs) {
            log(Log.DEBUG, null, message, args);
        }
    }

    public static void i(String message, Object... args) {
        log(Log.INFO, null, message, args);
    }

    public static void w(String message, Object... args) {
        log(Log.WARN, null, message, args);
    }

    public static void e(Throwable ex) {
        log(Log.ERROR, ex, null);
    }

    public static void e(String message, Object... args) {
        log(Log.ERROR, null, message, args);
    }

    public static void e(Throwable ex, String message, Object... args) {
        log(Log.ERROR, ex, message, args);
    }

    /**
     * 日志格式转换 以及输出
     *
     * @param priority
     * @param ex
     * @param message
     * @param args
     */
    private static void log(int priority, Throwable ex, String message, Object... args) {
        if (!writeLogs) {
            return;
        }
        if (args.length > 0) {
            message = String.format(message, args);
        }
        String log;
        if (ex == null) {
            log = message;
        } else {
            String logMessage = message == null ? ex.getMessage() : message;
            String logBody = Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }
        Log.println(priority, MediaLoader.TAG, log);
    }
}