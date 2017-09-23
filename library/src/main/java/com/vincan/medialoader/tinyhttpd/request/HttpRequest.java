package com.vincan.medialoader.tinyhttpd.request;

import com.vincan.medialoader.tinyhttpd.HttpHeaders;
import com.vincan.medialoader.tinyhttpd.HttpVersion;
import com.vincan.medialoader.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http请求
 * <br>
 * 举例：
 * <pre><code>
 * GET /videos/hello.mp4 HTTP/1.1
 * Range: bytes=0-1024
 * </pre></code>
 *
 * @author vincanyang
 */
public final class HttpRequest implements Request {

    private final HttpMethod mMethod;

    private final String mUrl;

    private final HttpVersion mVersion;

    private final HttpHeaders mHeaders;

    private final Map<String, List<String>> mParams;

    HttpRequest(Builder builder) {
        mMethod = builder.method;
        mUrl = builder.url;
        mVersion = builder.version;
        mHeaders = builder.headers;
        mParams = builder.params;
    }

    @Override
    public HttpMethod method() {
        return mMethod;
    }

    @Override
    public String url() {
        return mUrl;
    }

    @Override
    public HttpVersion protocol() {
        return mVersion;
    }

    @Override
    public HttpHeaders headers() {
        return mHeaders;
    }

    @Override
    public String getParam(String name) {
        List<String> param = mParams.get(name);
        return param != null ? param.get(0) : "";
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + mMethod +
                ", url=" + mUrl +
                ", protocol='" + mVersion + '\'' +
                '}';
    }

    public static final class Builder {

        private HttpMethod method;

        private String url;

        private HttpVersion version;

        private HttpHeaders headers;

        private Map<String, List<String>> params;

        public Builder() {
            this.method = HttpMethod.GET;
            this.version = HttpVersion.HTTP_1_1;
            this.headers = new HttpHeaders();
            this.params = new HashMap<>();
        }

        public Builder method(HttpMethod method) {
            this.method = Util.notEmpty(method);
            return this;
        }

        public Builder url(String url) {
            this.url = Util.notEmpty(url);
            return this;
        }

        public Builder version(HttpVersion version) {
            this.version = Util.notEmpty(version);
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = Util.notEmpty(headers);
            return this;
        }

        public Builder params(Map<String, List<String>> params) {
            this.params = Util.notEmpty(params);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}