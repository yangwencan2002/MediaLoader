package com.vincan.medialoader.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件相关工具类
 *
 * @author vincanyang
 */
public final class FileUtil {

    private FileUtil() {

    }

    /**
     * 获取缓存地址
     *
     * @param context
     * @return
     */
    public static File getDiskCacheDir(Context context) {
        File cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir();//sdcard/Android/data/${application package}/cache
        } else {
            cacheDir = context.getCacheDir();//data/data/${application package}/cache
        }
        return cacheDir;
    }

    public static void mkdirs(File dir) throws IOException {
        if (!Util.notEmpty(dir).exists()) {
            if (!dir.mkdirs()) {
                throw new IOException(String.format("Error create directory %s", dir.getAbsolutePath()));
            }
        }
    }

    public static List<File> getLruListFiles(File dir) {
        List<File> lruListFiles = new LinkedList<>();
        File[] listFiles = dir.listFiles();
        if (listFiles != null) {
            lruListFiles = Arrays.asList(listFiles);
            Collections.sort(lruListFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    long first = lhs.lastModified();
                    long second = rhs.lastModified();
                    return (first < second) ? -1 : ((first == second) ? 0 : 1);
                }
            });
        }
        return lruListFiles;
    }

    public static void updateLastModified(File file) throws IOException {
        if (file.exists()) {
            boolean isModified = file.setLastModified(System.currentTimeMillis()); //某些设备上setLastModified()会失效
            if (!isModified) {
                //ugly modify
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                long length = raf.length();
                raf.setLength(length + 1);
                raf.setLength(length);
                raf.close();
            }
        }
    }

    public static void cleanDir(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                cleanDir(listFile[i]);
                listFile[i].delete();
            }
        }
    }
}
