package com.vincan.medialoader;

import android.content.Context;

import com.vincan.medialoader.data.file.naming.FileNameCreator;
import com.vincan.medialoader.data.file.naming.Md5FileNameCreator;
import com.vincan.medialoader.utils.FileUtil;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link MediaLoader}初始化的默认配置
 *
 * @author vincanyang
 */
public final class DefaultConfigFactory {

    private static final String DIR_MEDIA_CACHE = "medialoader";

    /**
     * 最大缓存空间
     */
    public static final long DEFAULT_MAX_CACHE_FILES_SIZE = 500 * 1024 * 1024;//500MB

    /**
     * 最大缓存文件数
     */
    public static final int DEFAULT_MAX_CACHE_FILES_COUNT = 500;

    /**
     * 最大缓存期限
     */
    public static final long DEFAULT_MAX_CACHE_FILE_TIME_LIMIT = 10 * 24 * 60 * 60;//10Day

    /**
     * 下载线程池线程数量
     */
    public static final int DEFAULT_PROXY_DOWNLOAD_THREAD_POOL_SIZE = 3;

    /**
     * 下载线程池线程等级
     */
    public static final int DEFAULT_PROXY_DOWNLOAD_THREAD_PRIORITY = Thread.MAX_PRIORITY;//为保证体验，边下边播的下载线程优先级最高

    /**
     * 预下载线程池线程数量
     */
    public static final int DEFAULT_PRE_DOWNLOAD_THREAD_POOL_SIZE = 1;

    /**
     * 预下载线程池线程等级
     */
    public static final int DEFAULT_PRE_DOWNLOAD_THREAD_PRIORITY = Thread.MIN_PRIORITY;

    /**
     * 创建缓存根目录
     *
     * @param context
     * @return
     */
    public static File createCacheRootDir(Context context) {
        return createCacheRootDir(context, DIR_MEDIA_CACHE);
    }

    /**
     * 创建缓存根目录
     *
     * @param context
     * @param name
     * @return
     */
    public static File createCacheRootDir(Context context, String name) {
        return new File(FileUtil.getDiskCacheDir(context), name);
    }

    /**
     * 创建文件命名生成器
     *
     * @return
     */
    public static FileNameCreator createFileNameGenerator() {
        return new Md5FileNameCreator();
    }

    /**
     * 创建ExecutorService
     *
     * @param threadPoolSize
     * @param threadPriority
     * @return
     */
    public static ExecutorService createExecutorService(int threadPoolSize, int threadPriority) {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(threadPriority, "medialoader-pool-"));
    }

    /**
     * 创建预下载ExecutorService
     *
     * @return
     */
    public static ExecutorService createPredownloadExecutorService() {
        return createExecutorService(DEFAULT_PRE_DOWNLOAD_THREAD_POOL_SIZE, DEFAULT_PRE_DOWNLOAD_THREAD_PRIORITY);
    }

    /**
     * 创建缓存清理ExecutorService
     *
     * @return
     */
    public static ExecutorService createCleanupExecutorService() {
        return Executors.newSingleThreadExecutor(createThreadFactory(Thread.NORM_PRIORITY, "medialoader-pool-cleanup-"));
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
