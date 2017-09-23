package com.vincan.medialoader;

import android.content.Context;

import com.vincan.medialoader.data.DefaultDataSourceFactory;
import com.vincan.medialoader.data.file.cleanup.DiskLruCache;
import com.vincan.medialoader.data.file.naming.FileNameCreator;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * {@link MediaLoader}初始化的配置
 *
 * @author vincanyang
 */
public class MediaLoaderConfig {

    public final Context context;

    /**
     * 缓存根目录
     */
    public final File cacheRootDir;

    /**
     * 缓存文件名生成器
     */
    public final FileNameCreator cacheFileNameGenerator;

    /**
     * 磁盘缓存
     */
    public final DiskLruCache diskLruCache;

    /**
     * 最大缓存文件大小
     */
    public final long maxCacheFilesSize;

    /**
     * 最大缓存文件数
     */
    public final int maxCacheFilesCount;

    /**
     * 最大缓存期限
     */
    public long maxCacheFileTimeLimit;//TODO

    /**
     * 下载线程池线程数
     */
    public final int downloadThreadPoolSize;

    /**
     * 下载线程池线程等级
     */
    public final int downloadThreadPriority;

    /**
     * 下载ExecutorService
     */
    public final ExecutorService downloadExecutorService;

    MediaLoaderConfig(Builder builder) {
        context = builder.context;
        cacheRootDir = builder.cacheRootDir;
        cacheFileNameGenerator = builder.cacheFileNameGenerator;
        maxCacheFilesSize = builder.maxCacheFilesSize;
        maxCacheFilesCount = builder.maxCacheFilesCount;
        diskLruCache = DefaultDataSourceFactory.createDiskLruCache(this);
        downloadThreadPoolSize = builder.downloadThreadPoolSize;
        downloadThreadPriority = builder.downloadThreadPriority;
        downloadExecutorService = builder.downloadExecutorService;
    }

    /**
     * {@link MediaLoaderConfig}构造器
     */
    public static final class Builder {

        private Context context;

        private File cacheRootDir;

        private FileNameCreator cacheFileNameGenerator;

        private long maxCacheFilesSize = DefaultConfigFactory.DEFAULT_MAX_CACHE_FILES_SIZE;

        private int maxCacheFilesCount = DefaultConfigFactory.DEFAULT_MAX_CACHE_FILES_COUNT;

        private long maxCacheFileTimeLimit = DefaultConfigFactory.DEFAULT_MAX_CACHE_FILE_TIME_LIMIT;

        private int downloadThreadPoolSize = DefaultConfigFactory.DEFAULT_PROXY_DOWNLOAD_THREAD_POOL_SIZE;

        private int downloadThreadPriority = DefaultConfigFactory.DEFAULT_PROXY_DOWNLOAD_THREAD_PRIORITY;

        private ExecutorService downloadExecutorService;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * 设置缓存目录
         *
         * @param file
         * @return
         */
        public Builder cacheRootDir(File file) {
            cacheRootDir = Util.notEmpty(file);
            return this;
        }

        /**
         * 设置缓存文件命名生成器
         *
         * @param fileNameCreator
         * @return
         */
        public Builder cacheFileNameGenerator(FileNameCreator fileNameCreator) {
            cacheFileNameGenerator = Util.notEmpty(fileNameCreator);
            return this;
        }

        /**
         * 设置最大缓存空间
         *
         * @param size
         * @return
         */
        public Builder maxCacheFilesSize(long size) {
            maxCacheFilesSize = size;
            return this;
        }

        /**
         * 设置最大缓存文件数
         *
         * @param count
         * @return
         */
        public Builder maxCacheFilesCount(int count) {
            maxCacheFilesCount = count;
            return this;
        }

        /**
         * 设置最大缓存期限
         *
         * @param timeLimit
         * @return
         */
        public Builder maxCacheFileTimeLimit(long timeLimit) {
            maxCacheFileTimeLimit = timeLimit;
            return this;
        }

        /**
         * 设置下载线程池数量
         *
         * @param threadPoolSize
         * @return
         */
        public Builder downloadThreadPoolSize(int threadPoolSize) {
            this.downloadThreadPoolSize = threadPoolSize;
            return this;
        }

        /**
         * 设置下载线程优先级
         *
         * @param threadPriority
         * @return
         */
        public Builder downloadThreadPriority(int threadPriority) {
            if (threadPriority < Thread.MIN_PRIORITY) {
                this.downloadThreadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    this.downloadThreadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.downloadThreadPriority = threadPriority;
                }
            }
            return this;
        }

        /**
         * 设置下载ExecutorService
         *
         * @param executorService
         * @return
         */
        public Builder downloadExecutorService(ExecutorService executorService) {
            this.downloadExecutorService = executorService;
            return this;
        }

        /**
         * 创建{@link MediaLoaderConfig}实例
         *
         * @return
         */
        public MediaLoaderConfig build() {
            initNullFieldsWithDefault();
            return new MediaLoaderConfig(this);
        }

        private void initNullFieldsWithDefault() {
            if (cacheRootDir == null) {
                cacheRootDir = DefaultConfigFactory.createCacheRootDir(context);
            }
            if (cacheFileNameGenerator == null) {
                cacheFileNameGenerator = DefaultConfigFactory.createFileNameGenerator();
            }
            if (downloadExecutorService == null) {
                downloadExecutorService = DefaultConfigFactory.createExecutorService(downloadThreadPoolSize, downloadThreadPriority);
            }
        }
    }
}
