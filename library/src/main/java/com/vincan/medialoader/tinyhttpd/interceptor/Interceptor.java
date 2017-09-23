package com.vincan.medialoader.tinyhttpd.interceptor;

import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;

import java.io.IOException;

/**
 * 拦截器
 *
 * @author wencanyang
 */
public interface Interceptor {
    void intercept(Chain chain) throws ResponseException, IOException;

    /**
     * 拦截器链
     */
    interface Chain {
        Request request();

        Response response();

        void proceed(Request request, Response response) throws ResponseException, IOException;
    }
}