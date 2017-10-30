package com.vincan.medialoader;

import android.content.Context;
import android.util.Pair;

import com.vincan.medialoader.download.DownloadListener;
import com.vincan.medialoader.download.DownloadTask;
import com.vincan.medialoader.utils.FileUtil;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 下载管理者，用来实现预下载功能。
 * <br>使用举例：
 * <pre><code>
 *      DownloadManager.getInstance(getContext()).enqueue(new DownloadManager.Request(VIDEO_URL));
 * </code></pre>
 *
 * @author vincanyang
 */
public final class DownloadManager {

    private ExecutorService mDownloadExecutorService;

    private final Map<String, DownloadTask> mDownloaderTaskMap;

    private MediaLoaderConfig mMediaLoaderConfig;

    private volatile static DownloadManager sInstance;

    /**
     * 创建实例
     *
     * @param context
     * @return
     */
    public static DownloadManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new DownloadManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private DownloadManager(Context context) {
        mDownloaderTaskMap = new ConcurrentHashMap<>();
        mMediaLoaderConfig = MediaLoader.getInstance(context).mMediaLoaderConfig;//复用MediaLoader的MediaLoaderConfig，除MediaLoaderConfig.downloadExecutorService外
    }

    /**
     * 启动下载
     *
     * @param request
     */
    public void enqueue(Request request) {
        enqueue(request, null);
    }

    /**
     * 启动下载
     *
     * @param request  下载请求
     * @param listener 下载监听器
     */
    public void enqueue(Request request, DownloadListener listener) {
        Util.notEmpty(request);
        if (!request.forceDownload) {
            if (isCached(request.url)) {
                return;
            }
        }
        DownloadTask task = new DownloadTask(request, mMediaLoaderConfig, listener);
        mDownloaderTaskMap.put(request.url, task);
        if (mDownloadExecutorService == null) {
            mDownloadExecutorService = DefaultConfigFactory.createPredownloadExecutorService();
        }
        mDownloadExecutorService.submit(task);
    }

    /**
     * 下载是否正在运行
     *
     * @param url 文件url
     * @return
     */
    public boolean isRunning(String url) {
        DownloadTask task = mDownloaderTaskMap.get(Util.notEmpty(url));
        if (task != null) {
            return !task.isStopped();
        }
        return false;
    }

    /**
     * 暂停下载
     *
     * @param url 文件url
     */
    public void pause(String url) {
        DownloadTask task = mDownloaderTaskMap.get(Util.notEmpty(url));
        if (task != null) {
            task.pause();
        }
    }

    /**
     * 继续下载
     *
     * @param url 文件url
     */
    public void resume(String url) {
        DownloadTask task = mDownloaderTaskMap.get(Util.notEmpty(url));
        if (task != null) {
            task.resume();
        }
    }

    /**
     * 停止下载
     *
     * @param url 文件url
     */
    public void stop(String url) {
        DownloadTask task = mDownloaderTaskMap.remove(Util.notEmpty(url));
        if (task != null) {
            task.stop();
        }
    }

    /**
     * 暂停所有下载
     */
    public void pauseAll() {
        for (DownloadTask task : mDownloaderTaskMap.values()) {
            task.pause();
        }
    }

    /**
     * 继续所有下载
     */
    public void resumeAll() {
        for (DownloadTask task : mDownloaderTaskMap.values()) {
            task.resume();
        }
    }

    /**
     * 停止所有下载
     */
    public void stopAll() {
        for (DownloadTask task : mDownloaderTaskMap.values()) {
            task.stop();
        }
        mDownloaderTaskMap.clear();
        if (mDownloadExecutorService != null) {
            mDownloadExecutorService.shutdownNow();
            mDownloadExecutorService = null;
        }
    }

    /**
     * 是否缓存文件
     *
     * @param url 文件url
     * @return
     */
    public boolean isCached(String url) {
        return getCacheFile(url).exists();
    }

    /**
     * 获取缓存的文件
     *
     * @param url 文件url
     * @return
     */
    public File getCacheFile(String url) {
        return mMediaLoaderConfig.diskLruCache.get(Util.notEmpty(url));
    }

    /**
     * 清除缓存目录
     *
     * @throws IOException
     */
    public void cleanCacheDir() throws IOException {
        FileUtil.cleanDir(mMediaLoaderConfig.cacheRootDir);
    }

    /**
     * 下载请求封装类
     */
    public static class Request {

        private String url;

        private List<Pair<String, String>> requestHeaders = new ArrayList<Pair<String, String>>();

        private boolean forceDownload = false;

        public Request(String url) {
            this.url = Util.notEmpty(url);
        }

        /**
         * 获取url
         *
         * @return
         */
        public String getUrl() {
            return url;
        }

        /**
         * 获取请求头部
         *
         * @return
         */
        public List<Pair<String, String>> getRequestHeaders() {
            return requestHeaders;
        }

        /**
         * 添加请求头部信息
         *
         * @param header
         * @param value
         * @return
         */
        public Request addRequestHeader(String header, String value) {
            if (header == null) {
                throw new NullPointerException("header cannot be null");
            }
            if (header.contains(":")) {
                throw new IllegalArgumentException("header may not contain ':'");
            }
            if (value == null) {
                value = "";
            }
            requestHeaders.add(Pair.create(header, value));
            return this;
        }

        /**
         * 是否强制下载
         *
         * @param forceDownload true表示强制下载并覆盖本地文件，false表示本地存在则不下载
         * @return
         */
        public Request forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }
    }
}
