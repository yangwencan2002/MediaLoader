package com.vincan.medialoader.tinyhttpd.response;

/**
 * 响应状态
 *
 * @author vincanyang
 */
public enum HttpStatus {
    OK(200, "OK"),
    PARTIAL_CONTENT(206, "Partial Content"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    INTERNAL_ERROR(500, "Internal Server Error");

    public final int code;

    public final String desc;

    HttpStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return code + " " + desc;
    }
}