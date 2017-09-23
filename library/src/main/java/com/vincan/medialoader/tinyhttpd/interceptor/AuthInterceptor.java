package com.vincan.medialoader.tinyhttpd.interceptor;

import android.text.TextUtils;

import com.vincan.medialoader.tinyhttpd.HttpConstants;
import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;
import com.vincan.medialoader.tinyhttpd.response.HttpStatus;
import com.vincan.medialoader.utils.Util;

import java.io.IOException;

/**
 * 身份认证拦截器
 *
 * @author wencanyang
 */
public class AuthInterceptor implements Interceptor {

    private String mSecret;

    public AuthInterceptor(String secret) {
        mSecret = secret;
    }

    @Override
    public void intercept(Chain chain) throws ResponseException, IOException {
        Request request = chain.request();
        String clientSign = request.getParam(HttpConstants.PARAM_SIGN);
        if (TextUtils.isEmpty(clientSign)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, HttpConstants.PARAM_SIGN + " cann't be empty");
        }
        String clientTimestamp = request.getParam(HttpConstants.PARAM_TIMESTAMP);
        if (TextUtils.isEmpty(clientTimestamp)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, HttpConstants.PARAM_TIMESTAMP + " cann't be empty");
        }
        String serverSign = Util.getHmacSha1(request.url() + clientTimestamp, mSecret);
        if (!serverSign.equals(clientSign)) {
            throw new ResponseException(HttpStatus.UNAUTHORIZED, HttpConstants.PARAM_SIGN + " is not correct");
        }
        chain.proceed(request, chain.response());
    }
}
