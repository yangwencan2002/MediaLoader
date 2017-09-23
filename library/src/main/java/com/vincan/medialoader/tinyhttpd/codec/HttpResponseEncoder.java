package com.vincan.medialoader.tinyhttpd.codec;

import com.vincan.medialoader.tinyhttpd.HttpConstants;
import com.vincan.medialoader.tinyhttpd.response.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * {@link HttpResponse}的编码器
 *
 * @author vincanyang
 */
public class HttpResponseEncoder implements ResponseEncoder<HttpResponse> {

    @Override
    public byte[] encode(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(response.protocol().toString()).append(HttpConstants.SP).append(response.status().toString());
        sb.append(HttpConstants.CRLF);//status line end
        for (Map.Entry<String, String> entry : response.headers().entrySet()) {
            sb.append(entry.getKey()).append(HttpConstants.COLON).append(entry.getValue()).append(HttpConstants.CRLF);
        }
        sb.append(HttpConstants.CRLF);//headers end
        return sb.toString().getBytes();
    }
}
