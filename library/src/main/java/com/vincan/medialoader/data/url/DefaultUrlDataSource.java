package com.vincan.medialoader.data.url;

import android.text.TextUtils;

import com.vincan.medialoader.tinyhttpd.HttpHeaders;
import com.vincan.medialoader.utils.LogUtil;
import com.vincan.medialoader.utils.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;

/**
 * {@link UrlDataSource}的默认实现，使用HttpURLConnection进行网络通讯
 *
 * @author vincanyang
 */
public class DefaultUrlDataSource extends BaseUrlDataSource {

    private UrlDataSourceInfo mUrlDataSourceInfo;

    private HttpURLConnection mUrlConnection;

    private InputStream mInputStream;

    public DefaultUrlDataSource(String url) {
        UrlDataSourceInfo sourceInfo = mUrlDataSourceInfoCache.get(url);
        if (sourceInfo != null) {
            mUrlDataSourceInfo = sourceInfo;
        } else {
            mUrlDataSourceInfo = new UrlDataSourceInfo(url, Integer.MIN_VALUE, Util.getMimeTypeFromUrl(url));
        }
    }

    public DefaultUrlDataSource(DefaultUrlDataSource source) {
        this.mUrlDataSourceInfo = source.mUrlDataSourceInfo;
        this.mUrlDataSourceInfoCache = source.mUrlDataSourceInfoCache;
    }

    @Override
    public void open(long offset) throws IOException {
        try {
            LogUtil.d("Open connection " + (offset > 0 ? " with offset " + offset : "") + " to " + mUrlDataSourceInfo.url);
            mUrlConnection = (HttpURLConnection) new URL(mUrlDataSourceInfo.url).openConnection(Proxy.NO_PROXY);
            if (offset > 0) {
                mUrlConnection.setRequestProperty(HttpHeaders.Names.RANGE, "bytes=" + offset + "-");
            };
            String mime = mUrlConnection.getContentType();
            mInputStream = new BufferedInputStream(mUrlConnection.getInputStream(), Util.DEFAULT_BUFFER_SIZE);
            long length = readSourceAvailableBytes(mUrlConnection, offset, mUrlConnection.getResponseCode());
            this.mUrlDataSourceInfo = new UrlDataSourceInfo(mUrlDataSourceInfo.url, length, mime);
            this.mUrlDataSourceInfoCache.put(mUrlDataSourceInfo.url, mUrlDataSourceInfo);
        } catch (Exception e) {
            throw new IOException("Error opening connection for " + mUrlDataSourceInfo.url + " with offset " + offset, e);
        }
    }

    private long readSourceAvailableBytes(HttpURLConnection connection, long offset, int responseCode) throws IOException {
        int contentLength = connection.getContentLength();
        return responseCode == HTTP_OK ? contentLength
                : responseCode == HTTP_PARTIAL ? contentLength + offset : mUrlDataSourceInfo.length;
    }

    @Override
    public synchronized long length() throws IOException {
        if (mUrlDataSourceInfo.length == Integer.MIN_VALUE) {
            requestUrlDataSourceInfo();
        }
        return mUrlDataSourceInfo.length;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        try {
            return mInputStream.read(buffer, 0, buffer.length);
        } catch (IOException e) {
            throw new IOException("Error reading data from " + mUrlDataSourceInfo.url, e);
        }
    }

    @Override
    public synchronized String mimeType() throws IOException {
        if (TextUtils.isEmpty(mUrlDataSourceInfo.mimeType)) {
            requestUrlDataSourceInfo();
        }
        return mUrlDataSourceInfo.mimeType;
    }

    @Override
    public void close() throws IOException {
        if (mInputStream != null) {
            mInputStream.close();
        }
        if (mUrlConnection != null) {
            mUrlConnection.disconnect();
        }
        mUrlDataSourceInfoCache.remove(mUrlDataSourceInfo.url);
    }

    private void requestUrlDataSourceInfo() throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(mUrlDataSourceInfo.url).openConnection();
            urlConnection.setRequestMethod("HEAD");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            int length = urlConnection.getContentLength();
            String mime = urlConnection.getContentType();
            this.mUrlDataSourceInfo = new UrlDataSourceInfo(mUrlDataSourceInfo.url, length, mime);
            this.mUrlDataSourceInfoCache.put(mUrlDataSourceInfo.url, mUrlDataSourceInfo);
            LogUtil.d("requestUrlDataSourceInfo: " + mUrlDataSourceInfo.toString());
        } catch (IOException e) {
            LogUtil.e("Error request addHeader info from " + mUrlDataSourceInfo.url, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public String getUrl() {
        return mUrlDataSourceInfo.url;
    }
}
