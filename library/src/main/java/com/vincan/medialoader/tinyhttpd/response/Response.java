package com.vincan.medialoader.tinyhttpd.response;

import com.vincan.medialoader.tinyhttpd.HttpVersion;
import com.vincan.medialoader.tinyhttpd.HttpHeaders;

import java.io.IOException;

/**
 * 响应接口
 *
 * @author vincanyang
 */
public interface Response {

    HttpStatus status();

    void setStatus(HttpStatus status);

    HttpVersion protocol();

    HttpHeaders headers();

    /**
     * 添加头部
     *
     * @param key   {@link com.vincan.medialoader.tinyhttpd.HttpHeaders.Names}
     * @param value
     */
    void addHeader(String key, String value);

    void write(byte[] bytes) throws IOException;

    void write(byte[] bytes, int offset, int length) throws IOException;
}
