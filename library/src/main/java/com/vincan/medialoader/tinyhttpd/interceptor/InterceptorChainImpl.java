package com.vincan.medialoader.tinyhttpd.interceptor;

import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.tinyhttpd.response.ResponseException;

import java.io.IOException;
import java.util.List;

/**
 * 拦截器链实现
 *
 * @author vincanyang
 */
public class InterceptorChainImpl implements Interceptor.Chain {

    private List<Interceptor> mInterceptors;

    private int mIndex;

    private Request mRequest;

    private Response mResponse;

    public InterceptorChainImpl(List<Interceptor> interceptors, int index, Request request, Response response) {
        mInterceptors = interceptors;
        mIndex = index;
        mRequest = request;
        mResponse = response;
    }

    @Override
    public Request request() {
        return mRequest;
    }

    @Override
    public Response response() {
        return mResponse;
    }

    @Override
    public void proceed(Request request, Response response) throws ResponseException, IOException {
        if (mIndex >= mInterceptors.size()) {
            throw new AssertionError();
        }
        InterceptorChainImpl next = new InterceptorChainImpl(
                mInterceptors, mIndex + 1, request, response);
        Interceptor interceptor = mInterceptors.get(mIndex);
        interceptor.intercept(next);
    }
}
