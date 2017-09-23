package com.vincan.medialoader.download;

import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.data.DefaultDataSourceFactory;
import com.vincan.medialoader.data.file.FileDataSource;
import com.vincan.medialoader.data.url.UrlDataSource;
import com.vincan.medialoader.utils.LogUtil;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 下载任务
 *
 * @author vincanyang
 */
public class DownloadTask implements Runnable {

    private UrlDataSource mUrlDataSource;

    private FileDataSource mFileDataSource;

    private final Object mStopLock = new Object();
    private volatile boolean stopped = false;

    private final Object mPauseLock = new Object();
    private final AtomicBoolean mPaused = new AtomicBoolean(false);

    private volatile int mDownloadPercent;

    private DownloadListener mDownloadListener;

    public DownloadTask(DownloadManager.Request request, MediaLoaderConfig mediaLoaderConfig, DownloadListener listener) {
        mUrlDataSource = DefaultDataSourceFactory.createUrlDataSource(request.getUrl());
        mFileDataSource = DefaultDataSourceFactory.createFileDataSource(new File(mediaLoaderConfig.cacheRootDir, mediaLoaderConfig.cacheFileNameGenerator.create(request.getUrl())), mediaLoaderConfig.diskLruCache);
        mDownloadListener = listener;
    }

    public DownloadTask(UrlDataSource urlDataSource, FileDataSource fileDataSource, DownloadListener listener) {
        mUrlDataSource = urlDataSource;
        mFileDataSource = fileDataSource;
        mDownloadListener = listener;
    }

    @Override
    public void run() {
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);//避免和主线程抢占资源发生ANR
        long urlDataSourceLength;
        long offset;
        try {
            offset = mFileDataSource.length();
            mUrlDataSource.open(offset);
            urlDataSourceLength = mUrlDataSource.length();
            byte[] buffer = new byte[Util.DEFAULT_BUFFER_SIZE];
            int readBytes;
            while ((readBytes = mUrlDataSource.read(buffer)) != -1) {
                waitIfPaused();
                synchronized (mStopLock) {
                    if (isStopped()) {
                        return;
                    }
                    mFileDataSource.append(buffer, readBytes);
                }
                offset += readBytes;
                notifyNewDataAvailable(offset, urlDataSourceLength);
            }
            tryComplete();
        } catch (Throwable e) {
            if (mDownloadListener != null) {
                mDownloadListener.onError(e);
            }
            LogUtil.e(e);
        } finally {
            try {
                mUrlDataSource.close();
            } catch (IOException e) {
                LogUtil.e("error close url data source ", e);
            }
        }
    }

    private void onDownloadPercentUpdated(int downloadProgress) {
        if (mDownloadListener != null) {
            mDownloadListener.onProgress(mUrlDataSource.getUrl(), mFileDataSource.getFile(), downloadProgress);
//            LogUtil.e("Url " + mUrlDataSource.getUrl() + " download progress:" + downloadProgress);
        }
    }

    private void notifyNewDataAvailable(long fileDataSourceAvailable, long urlDataSourceLength) {
        int newPercent = (urlDataSourceLength == 0) ? 100 : (int) (fileDataSourceAvailable * 100 / urlDataSourceLength);
        if (newPercent > mDownloadPercent + 2) {//防止通知太快导致ui掉帧
            onDownloadPercentUpdated(newPercent);
            mDownloadPercent = newPercent;
        }
    }

    private void tryComplete() throws IOException {
        mDownloadPercent = 100;
        onDownloadPercentUpdated(mDownloadPercent);
        synchronized (mStopLock) {
            if (!isStopped() && mFileDataSource.length() == mUrlDataSource.length()) {
                mFileDataSource.complete();
            }
        }
    }

    public boolean isStopped() {
        return Thread.currentThread().isInterrupted() || stopped;
    }

    private void waitIfPaused() {
        if (mPaused.get()) {
            synchronized (mPauseLock) {
                try {
                    mPauseLock.wait();
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public void pause() {
        mPaused.set(true);
    }

    public void resume() {
        mPaused.set(false);
        synchronized (mPauseLock) {
            mPauseLock.notifyAll();
        }
    }

    public Thread getCurrentThread() {
        return Thread.currentThread();
    }

    public void stop() {
        synchronized (mStopLock) {
            stopped = true;
            try {
                mFileDataSource.close();
            } catch (IOException e) {
                LogUtil.e("error close file dataSource", e);
            }
        }
    }
}
