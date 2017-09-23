package com.vincan.medialoader;

import android.content.Context;
import android.net.Uri;

import com.vincan.medialoader.controller.MediaProxyHttpd;
import com.vincan.medialoader.download.DownloadListener;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.io.IOException;

/**
 * MediaLoader组件入口类，所有API都将通过它访问。
 * <br>使用举例：
 * <pre><code>
 *      String proxyUrl = MediaLoader.getInstance(getContext()).getProxyUrl(VIDEO_URL);
 *      videoView.setVideoPath(proxyUrl);
 * </code></pre>
 *
 * @author vincanyang
 */
public final class MediaLoader {

    public static final String TAG = MediaLoader.class.getSimpleName();

    private volatile static MediaLoader sInstance;

    MediaLoaderConfig mMediaLoaderConfig;

    private MediaProxyHttpd mMediaHttpd;

    /**
     * 创建实例
     *
     * @param context
     * @return
     */
    public static MediaLoader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MediaLoader.class) {
                if (sInstance == null) {
                    sInstance = new MediaLoader(Util.notEmpty(context).getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private MediaLoader(Context context) {
        try {
            mMediaHttpd = new MediaProxyHttpd();
            init(new MediaLoaderConfig.Builder(context).build());//使用默认的VideoLoaderConfig
        } catch (InterruptedException | IOException e) {
            destroy();
            throw new IllegalStateException("error init medialoader", e);
        }
    }

    /**
     * 初始化设置
     *
     * @param mediaLoaderConfig 设置选项
     */
    public void init(MediaLoaderConfig mediaLoaderConfig) {
        mMediaLoaderConfig = Util.notEmpty(mediaLoaderConfig);
        mMediaHttpd.setVideoLoaderConfig(mediaLoaderConfig);
    }

    /**
     * 获取代理url
     *
     * @param url 原url
     * @return
     */
    public String getProxyUrl(String url) {
        return getProxyUrl(url, true);
    }

    /**
     * 获取代理url
     *
     * @param url
     * @return
     */
    private String getProxyUrl(String url, boolean isAllowUriFromFile) {
        if (isAllowUriFromFile) {
            File file = getCacheFile(url);
            if (file.exists()) {
                return Uri.fromFile(file).toString();//originFile://url
            }
        }
        if (mMediaHttpd.isWorking()) {
            return mMediaHttpd.createUrl(url);//http://127.0.0.1:8090/path
        } else {
            return url;
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
     * 添加下载监听器
     *
     * @param url      文件url
     * @param listener 下载监听器
     */
    public void addDownloadListener(String url, DownloadListener listener) {
        mMediaHttpd.addDownloadListener(Util.notEmpty(url), Util.notEmpty(listener));
    }

    /**
     * 删除下载监听器
     *
     * @param url      文件url
     * @param listener 下载监听器
     */
    public void removeDownloadListener(String url, DownloadListener listener) {
        mMediaHttpd.removeDownloadListener(Util.notEmpty(url), Util.notEmpty(listener));
    }

    /**
     * 删除下载监听器（当你无法得知文件url时使用）
     *
     * @param listener 下载监听器
     */
    public void removeDownloadListener(DownloadListener listener) {
        mMediaHttpd.removeDownloadListener(Util.notEmpty(listener));
    }

    /**
     * 暂停下载
     *
     * @param url 文件url
     */
    public void pauseDownload(String url) {
        mMediaHttpd.pauseDownload(Util.notEmpty(url));
    }

    /**
     * 继续下载
     *
     * @param url 文件url
     */
    public void resumeDownload(String url) {
        mMediaHttpd.resumeDownload(Util.notEmpty(url));
    }

    /**
     * 销毁实例
     */
    public void destroy() {
        if (mMediaHttpd != null) {
            mMediaHttpd.shutdown();
            mMediaHttpd = null;
        }
        if (mMediaLoaderConfig != null) {
            mMediaLoaderConfig.downloadExecutorService.shutdownNow();
            mMediaLoaderConfig = null;
        }
    }
}
