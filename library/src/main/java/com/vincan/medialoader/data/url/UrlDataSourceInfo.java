package com.vincan.medialoader.data.url;

/**
 * {@link UrlDataSource}信息
 *
 * @author vincanyang
 */
public class UrlDataSourceInfo {

    public final String url;

    public final long length;

    public final String mimeType;

    public UrlDataSourceInfo(String url, long length, String mimeType) {
        this.url = url;
        this.length = length;
        this.mimeType = mimeType;
    }
}