package com.vincan.medialoader.tinyhttpd.response;


import com.vincan.medialoader.tinyhttpd.HttpVersion;
import com.vincan.medialoader.tinyhttpd.HttpHeaders;
import com.vincan.medialoader.utils.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * http响应
 *
 * @author vincanyang
 */
public final class HttpResponse implements Response {

    private HttpHeaders mHeaders = new HttpHeaders();

    private final HttpVersion mHttpVersion = HttpVersion.HTTP_1_1;

    private HttpStatus mStatus = HttpStatus.OK;

    private final SocketChannel mChannel;

    private ByteBuffer mResponseByteBuffer = ByteBuffer.allocate(Util.DEFAULT_BUFFER_SIZE);

    public HttpResponse(SocketChannel channel) {
        mChannel = channel;
    }

    @Override
    public void setStatus(HttpStatus status) {
        mStatus = status;
    }

    @Override
    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        mResponseByteBuffer.put(bytes, offset, length);
        mResponseByteBuffer.flip();
        while (mResponseByteBuffer.hasRemaining()) {//XXX 巨坑：ByteBuffer会缓存，可能不会全部写入channel
            mChannel.write(mResponseByteBuffer);
        }
        mResponseByteBuffer.clear();
    }

    @Override
    public HttpStatus status() {
        return mStatus;
    }

    @Override
    public HttpVersion protocol() {
        return mHttpVersion;
    }

    @Override
    public HttpHeaders headers() {
        return mHeaders;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpVersion=" + mHttpVersion +
                ", status=" + mStatus +
                '}';
    }
}