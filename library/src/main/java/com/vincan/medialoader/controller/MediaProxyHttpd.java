package com.vincan.medialoader.controller;

import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.download.DownloadListener;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.tinyhttpd.TinyHttpd;
import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;
import com.vincan.medialoader.utils.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http服务器，将请求分发到{@link MediaController}进行处理
 *
 * @author vincanyang
 */
public class MediaProxyHttpd extends TinyHttpd {

    private final Object mLock = new Object();

    /**
     * 相当于web.xml，用于存储url和handler之间的映射
     */
    private final Map<String, MediaController> mReuqestHandlerMap = new ConcurrentHashMap<>();

    private MediaLoaderConfig mMediaLoaderConfig;

    public MediaProxyHttpd() throws InterruptedException, IOException {
        this(null);
    }

    public MediaProxyHttpd(MediaLoaderConfig videoLoaderConfig) throws InterruptedException, IOException {
        super();
        mMediaLoaderConfig = videoLoaderConfig;
    }

    @Override
    public void doGet(Request request, Response response) throws ResponseException, IOException {
        if (DownloadManager.getInstance(mMediaLoaderConfig.context).isRunning(request.url())) {//如果预下载正在进行，则停止它，让边下边播的下载来
            DownloadManager.getInstance(mMediaLoaderConfig.context).stop(request.url());
            LogUtil.d("Url " + request.url() + " is preDownloading,now be canceled by proxy download");
        }
        getMediaController(request.url()).responseByRequest(request, response);
    }

    public void setVideoLoaderConfig(MediaLoaderConfig videoLoaderConfig) {
        mMediaLoaderConfig = videoLoaderConfig;
    }

    private MediaController getMediaController(String url) {
        synchronized (mLock) {//多个线程安全的操作仍需要同步，其他地方同
            MediaController client = mReuqestHandlerMap.get(url);
            if (client == null) {
                client = new MediaController(url, mMediaLoaderConfig);
                mReuqestHandlerMap.put(url, client);
            }
            return client;
        }
    }

    private void destroyMediaControllers() {
        synchronized (mLock) {
            for (MediaController clients : mReuqestHandlerMap.values()) {
                clients.destroy();
            }
            mReuqestHandlerMap.clear();
        }
    }

    public void pauseDownload(String url) {
        getMediaController(url).pauseDownload(url);
    }

    public void resumeDownload(String url) {
        getMediaController(url).resumeDownload(url);
    }

    public void addDownloadListener(String url, DownloadListener listener) {
        getMediaController(url).addDownloadListener(listener);
    }

    public void removeDownloadListener(String url, DownloadListener listener) {
        getMediaController(url).removeDownloadListener(listener);
    }

    public void removeDownloadListener(DownloadListener listener) {
        for (MediaController requestHandler : mReuqestHandlerMap.values()) {
            requestHandler.removeDownloadListener(listener);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        destroyMediaControllers();
    }
}
