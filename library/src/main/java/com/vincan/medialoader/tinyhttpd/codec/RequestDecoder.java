package com.vincan.medialoader.tinyhttpd.codec;

import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;

/**
 * {@link Request}的解码器
 *
 * @author vincanyang
 */
public interface RequestDecoder<T extends Request> {

    T decode(byte[] bytes) throws ResponseException;
}
