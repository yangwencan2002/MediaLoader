package com.vincan.medialoader.tinyhttpd.codec;

import com.vincan.medialoader.tinyhttpd.response.Response;

import java.io.IOException;

/**
 * {@link Response}的编码器
 *
 * @author vincanyang
 */
public interface ResponseEncoder<T extends Response> {

    byte[] encode(T response) throws IOException;
}
