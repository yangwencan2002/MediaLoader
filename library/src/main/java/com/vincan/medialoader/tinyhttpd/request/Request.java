package com.vincan.medialoader.tinyhttpd.request;

import com.vincan.medialoader.tinyhttpd.HttpHeaders;
import com.vincan.medialoader.tinyhttpd.HttpVersion;

/**
 * 请求接口
 *
 * @author vincanyang
 */
public interface Request {

    HttpMethod method();

    String url();

    HttpVersion protocol();

    HttpHeaders headers();

    String getParam(String name);
}
