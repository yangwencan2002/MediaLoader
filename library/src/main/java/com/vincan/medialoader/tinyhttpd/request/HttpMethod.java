package com.vincan.medialoader.tinyhttpd.request;

import java.io.IOException;

/**
 * http方法
 *
 * @author vincanyang
 */
public enum HttpMethod {
    GET("GET"),

    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod get(String method) throws IOException {
        if (method.equals(GET.method)) {
            return GET;
        } else if (method.equals(POST.method)) {
            return POST;
        }
        throw new IOException("Unexpected method: " + method);
    }

    @Override
    public String toString() {
        return method;
    }
}