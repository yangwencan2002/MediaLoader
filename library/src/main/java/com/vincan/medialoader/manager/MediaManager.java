package com.vincan.medialoader.manager;

import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;

import java.io.IOException;

/**
 * Media业务接口
 *
 * @author vincanyang
 */
public interface MediaManager {

    void responseByRequest(Request request, Response response) throws ResponseException, IOException;

    void pauseDownload(String url);

    void resumeDownload(String url);

    void destroy();
}
