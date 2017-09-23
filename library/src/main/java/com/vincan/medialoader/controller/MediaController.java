package com.vincan.medialoader.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.manager.MediaManager;
import com.vincan.medialoader.manager.MediaManagerImpl;
import com.vincan.medialoader.data.url.UrlDataSource;
import com.vincan.medialoader.download.DownloadListener;
import com.vincan.medialoader.data.file.FileDataSource;
import com.vincan.medialoader.data.DefaultDataSourceFactory;
import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Media请求控制器
 *
 * @author vincanyang
 */
public class MediaController {

    private final List<DownloadListener> mCacheListeners = Collections.synchronizedList(new LinkedList<DownloadListener>());

    private final String mUrl;

    private final MediaLoaderConfig mMediaLoaderConfig;

    private MediaManager mMediaManager;

    public MediaController(String url, MediaLoaderConfig mediaLoaderConfig) {
        mUrl = url;
        mMediaLoaderConfig = mediaLoaderConfig;
    }

    public void responseByRequest(Request request, Response response) throws ResponseException, IOException {
        if (mMediaManager == null) {
            UrlDataSource urlDataSource = DefaultDataSourceFactory.createUrlDataSource(mUrl);
            FileDataSource fileDataSource = DefaultDataSourceFactory.createFileDataSource(new File(mMediaLoaderConfig.cacheRootDir, mMediaLoaderConfig.cacheFileNameGenerator.create(mUrl)), mMediaLoaderConfig.diskLruCache);
            mMediaManager = new MediaManagerImpl(urlDataSource, fileDataSource, new MainThreadCacheListener(mUrl, mCacheListeners), mMediaLoaderConfig.downloadExecutorService);
        }
        try {
            mMediaManager.responseByRequest(request, response);
        } finally {
            mMediaManager.destroy();
            mMediaManager = null;
        }
    }

    public void pauseDownload(String url) {
        if (mMediaManager != null) {
            mMediaManager.pauseDownload(url);
        }
    }

    public void resumeDownload(String url) {
        if (mMediaManager != null) {
            mMediaManager.resumeDownload(url);
        }
    }

    public void addDownloadListener(DownloadListener listener) {
        mCacheListeners.add(listener);
    }

    public void removeDownloadListener(DownloadListener listener) {
        mCacheListeners.remove(listener);
    }

    public void destroy() {
        mCacheListeners.clear();
        if (mMediaManager != null) {
            mMediaManager.destroy();
            mMediaManager = null;
        }
    }

    private static final class MainThreadCacheListener extends Handler implements DownloadListener {

        private final String url;
        private final List<DownloadListener> listeners;

        public MainThreadCacheListener(String url, List<DownloadListener> listeners) {
            super(Looper.getMainLooper());
            this.url = url;
            this.listeners = listeners;
        }

        @Override
        public void onProgress(String url, File file, int progress) {
            Message message = obtainMessage();
            message.arg1 = progress;
            message.obj = file;
            sendMessage(message);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void handleMessage(Message msg) {
            for (DownloadListener cacheListener : listeners) {
                cacheListener.onProgress(url, (File) msg.obj, msg.arg1);
            }
        }
    }
}
