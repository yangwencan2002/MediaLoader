package com.vincan.medialoader.data.file.cleanup;

import com.vincan.medialoader.DefaultConfigFactory;
import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.utils.FileUtil;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 磁盘LRU缓存的简单实现
 *
 * @author vincanyang
 */
public class SimpleDiskLruCache implements DiskLruCache {

    private final Executor mCleanupExecutor;

    private final MediaLoaderConfig mMediaLoaderConfig;

    public SimpleDiskLruCache(MediaLoaderConfig mediaLoaderConfig) {
        mMediaLoaderConfig = mediaLoaderConfig;
        mCleanupExecutor = DefaultConfigFactory.createCleanupExecutorService();
    }

    @Override
    public File get(String url) {
        File cacheDir = mMediaLoaderConfig.cacheRootDir;
        String fileName = mMediaLoaderConfig.cacheFileNameGenerator.create(Util.notEmpty(url));
        File file = new File(cacheDir, fileName);
        mCleanupExecutor.execute(new CleanupRunnable(file));
        return file;
    }

    private void cleanup(List<File> files) {
        long totalFilesSize = countTotalSize(files);
        int totalFilesCount = files.size();
        for (File file : files) {
            boolean reserved = (totalFilesCount <= mMediaLoaderConfig.maxCacheFilesCount && totalFilesSize <= mMediaLoaderConfig.maxCacheFilesSize);
            if (!reserved) {
                long fileSize = file.length();
                boolean deleted = file.delete();
                if (deleted) {
                    totalFilesSize -= fileSize;
                    totalFilesCount--;
                }
            }
        }
    }

    private long countTotalSize(List<File> files) {
        long totalSize = 0;
        for (int i = 0; i < files.size(); i++) {
            totalSize += files.get(i).length();
        }
        return totalSize;
    }

    private class CleanupRunnable implements Runnable {

        private final File file;

        public CleanupRunnable(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);//避免和主线程抢占资源发生ANR
            try {
                FileUtil.updateLastModified(file);
            } catch (IOException e) {
            }
            cleanup(FileUtil.getLruListFiles(file.getParentFile()));
        }
    }

    @Override
    public void save(String url, File file) {

    }

    @Override
    public void remove(String url) {

    }

    @Override
    public void close() {

    }

    @Override
    public void clear() {

    }
}
