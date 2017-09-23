package com.vincan.medialoader.tinyhttpd.response;

/**
 * 响应异常
 *
 * @author vincanyang
 */
public final class ResponseException extends Exception {

    private final HttpStatus status;

    public ResponseException(HttpStatus status) {
        super(status.desc);
        this.status = status;
    }

    public ResponseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ResponseException(HttpStatus status, String message, Exception e) {
        super(message, e);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}