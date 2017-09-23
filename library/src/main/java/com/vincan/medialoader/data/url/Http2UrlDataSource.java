package com.vincan.medialoader.data.url;

import java.io.IOException;

/**
 * Http2实现
 * //TODO
 *
 * @author vincanyang
 */
public class Http2UrlDataSource extends BaseUrlDataSource {
    @Override
    public void open(long offset) throws IOException {

    }

    @Override
    public long length() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return 0;
    }

    @Override
    public String mimeType() throws IOException {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
