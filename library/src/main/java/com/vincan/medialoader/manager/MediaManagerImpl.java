package com.vincan.medialoader.manager;

import android.text.TextUtils;

import com.vincan.medialoader.data.DefaultDataSourceFactory;
import com.vincan.medialoader.data.file.FileDataSource;
import com.vincan.medialoader.data.url.DefaultUrlDataSource;
import com.vincan.medialoader.data.url.UrlDataSource;
import com.vincan.medialoader.download.DownloadListener;
import com.vincan.medialoader.download.DownloadTask;
import com.vincan.medialoader.tinyhttpd.HttpHeaders;
import com.vincan.medialoader.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.medialoader.tinyhttpd.codec.ResponseEncoder;
import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;
import com.vincan.medialoader.tinyhttpd.response.HttpStatus;
import com.vincan.medialoader.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Media业务实现
 *
 * @author vincanyang
 */
public class MediaManagerImpl implements MediaManager {

    private static final float NO_CACHE_BARRIER = .2f;

    private UrlDataSource mUrlDataSource;

    private FileDataSource mFileDataSource;

    private DownloadListener mDownloadListener;

    private final Object mWaitForDownloadLock = new Object();

    private volatile Thread mDownloadThread;

    private final DownloadTask mDownloadTask;

    private final ExecutorService mDownloadExecutorService;

    private ResponseEncoder mResponseEncoder = new HttpResponseEncoder();

    public MediaManagerImpl(UrlDataSource urlDataSource, FileDataSource fileDataSource, DownloadListener downloadListener, ExecutorService downloadExecutorService) {
        mUrlDataSource = urlDataSource;
        mFileDataSource = fileDataSource;
        mDownloadListener = downloadListener;
        mDownloadTask = new DownloadTask(mUrlDataSource, mFileDataSource, new DownloadListenerImpl());
        mDownloadExecutorService = downloadExecutorService;
    }

    @Override
    public void responseByRequest(Request request, Response response) throws ResponseException, IOException {
        addResponseHeaders(request, response);
        byte[] headersBytes = mResponseEncoder.encode(response);
        response.write(headersBytes);

        long rangeOffset = request.headers().getRangeOffset();
        if (isCacheDataEnough(request)) {
            responseWithCache(rangeOffset, response);
        } else {
            responseWithUrl(rangeOffset, response);
        }
    }

    private void addResponseHeaders(Request request, Response response) throws IOException {
        response.setStatus(request.headers().isPartial() ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK);
        response.addHeader(HttpHeaders.Names.ACCEPT_RANGES, HttpHeaders.Values.BYTES);
        long length = mFileDataSource.isCompleted() ? mFileDataSource.length() : mUrlDataSource.length();
        if (length >= 0) {
            long contentLength = request.headers().isPartial() ? length - request.headers().getRangeOffset() : length;
            response.addHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(contentLength));
        }
        if (length >= 0 && request.headers().isPartial()) {
            response.addHeader(HttpHeaders.Names.CONTENT_RANGE, String.format(HttpHeaders.Values.BYTES + " %d-%d/%d", request.headers().getRangeOffset(), length - 1, length));
        }
        String mimeType = mUrlDataSource.mimeType();
        if (!TextUtils.isEmpty(mimeType)) {
            response.addHeader(HttpHeaders.Names.CONTENT_TYPE, mimeType);
        }
    }

    private boolean isCacheDataEnough(Request request) throws IOException {
        long urlDataSourceLength = mUrlDataSource.length();
        boolean sourceLengthKnown = urlDataSourceLength > 0;
        long fileDataSourceLength = mFileDataSource.length();
        return !sourceLengthKnown || !request.headers().isPartial() || request.headers().getRangeOffset() <= fileDataSourceLength + urlDataSourceLength * NO_CACHE_BARRIER;
    }

    private void responseWithCache(long rangeOffset, Response response) throws ResponseException, IOException {
        byte[] buffer = new byte[Util.DEFAULT_BUFFER_SIZE];
        int readBytes;
        while ((readBytes = seekAndRead(rangeOffset, buffer, buffer.length)) != -1) {
            response.write(buffer, 0, readBytes);
            rangeOffset += readBytes;
        }
    }

    private int seekAndRead(long rangeOffset, byte[] buffer, int length) throws ResponseException, IOException {
        checkStartDownload();//启动下载
        while (!mFileDataSource.isCompleted() && mFileDataSource.length() < (rangeOffset + length)) {//如果要访问的数据量不够且文件未完整，都需要等待下载
            waitForDownload();
        }
        return mFileDataSource.seekAndRead(rangeOffset, buffer);
    }

    private synchronized void checkStartDownload() throws IOException {
        boolean isThreadStarted = mDownloadThread != null && mDownloadThread.getState() != Thread.State.TERMINATED;
        if (!mFileDataSource.isCompleted() && !mDownloadTask.isStopped() && !isThreadStarted) {//如果文件未下载完成，则必然要启动下载
            mDownloadExecutorService.submit(mDownloadTask);
            mDownloadThread = mDownloadTask.getCurrentThread();
        }
    }

    private void waitForDownload() throws ResponseException {
        synchronized (mWaitForDownloadLock) {
            try {
                mWaitForDownloadLock.wait(1000);
            } catch (InterruptedException e) {
                throw new ResponseException(HttpStatus.INTERNAL_ERROR, "Waiting for downloading is interrupted");
            }
        }
    }

    private final class DownloadListenerImpl implements DownloadListener {

        @Override
        public void onProgress(String url, File file, int progress) {
            synchronized (mWaitForDownloadLock) {
                mWaitForDownloadLock.notifyAll();
            }
            mDownloadListener.onProgress(url, file, progress);
        }

        @Override
        public void onError(Throwable e) {
            mDownloadListener.onError(e);
        }
    }

    private void responseWithUrl(long rangeOffset, Response response) throws IOException {
        UrlDataSource newDataSource = DefaultDataSourceFactory.createUrlDataSource((DefaultUrlDataSource) mUrlDataSource);
        try {
            newDataSource.open(rangeOffset);
            byte[] buffer = new byte[Util.DEFAULT_BUFFER_SIZE];
            int readBytes;
            while ((readBytes = newDataSource.read(buffer)) != -1) {
                response.write(buffer, 0, readBytes);
            }
        } finally {
            newDataSource.close();
        }
    }

    @Override
    public void pauseDownload(String url) {
        if (mDownloadTask != null) {
            mDownloadTask.pause();
        }
    }

    @Override
    public void resumeDownload(String url) {
        if (mDownloadTask != null) {
            mDownloadTask.resume();
        }
    }

    @Override
    public void destroy() {
        mDownloadTask.stop();
        if (mDownloadThread != null) {
            mDownloadThread.interrupt();
        }
    }
}
