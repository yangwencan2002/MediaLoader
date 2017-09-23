package com.vincan.medialoader.tinyhttpd;

import java.io.IOException;

/**
 * http版本号
 *
 * @author vincanyang
 */
public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),

    HTTP_1_1("HTTP/1.1"),

    HTTP_2("H2");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion get(String version) throws IOException {
        if (version.equals(HTTP_1_0.version)) {
            return HTTP_1_0;
        } else if (version.equals(HTTP_1_1.version)) {
            return HTTP_1_1;
        } else if (version.equals(HTTP_2.version)) {
            return HTTP_2;
        }
        throw new IOException("Unexpected version: " + version);
    }

    @Override
    public String toString() {
        return version;
    }
}