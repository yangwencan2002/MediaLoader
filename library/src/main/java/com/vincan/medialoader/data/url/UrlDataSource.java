package com.vincan.medialoader.data.url;

import java.io.Closeable;
import java.io.IOException;

/**
 * URL数据源接口
 *
 * @author vincanyang
 */
public interface UrlDataSource extends Closeable {

    void open(long offset) throws IOException;

    long length() throws IOException;

    int read(byte[] buffer) throws IOException;

    String mimeType() throws IOException;

    String getUrl();
}
