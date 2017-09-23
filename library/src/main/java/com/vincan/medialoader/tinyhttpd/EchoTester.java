package com.vincan.medialoader.tinyhttpd;

import com.vincan.medialoader.data.DefaultDataSourceFactory;
import com.vincan.medialoader.data.url.UrlDataSource;
import com.vincan.medialoader.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.medialoader.tinyhttpd.codec.ResponseEncoder;
import com.vincan.medialoader.tinyhttpd.request.Request;
import com.vincan.medialoader.tinyhttpd.response.Response;
import com.vincan.medialoader.utils.LogUtil;
import com.vincan.medialoader.utils.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * httpd的echo测试,检测httpd是否运行
 *
 * @author vincanyang
 */
public class EchoTester {

    private static final String URL = "echo";

    private static final String RESPONSE = "echo ok";

    private static final long TIMEOUT_AWAIT = 300;//300ms

    private final String mHost;

    private final int mPort;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();//FIXME

    private ResponseEncoder mResponseEncoder = new HttpResponseEncoder();

    public EchoTester(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public boolean isEchoRequest(Request request) {
        return URL.equals(request.url());
    }

    public boolean request() {
        Future<Boolean> echoFutureTask = mExecutorService.submit(new RequestEchoCallable());
        try {
            return echoFutureTask.get(TIMEOUT_AWAIT, MILLISECONDS);
        } catch (TimeoutException e) {
            LogUtil.e("Echo httpd timeout " + TIMEOUT_AWAIT, e);
        } catch (InterruptedException | ExecutionException e) {
            LogUtil.e("Error echo httpd", e);
        }
        return false;
    }

    private boolean requestEcho() throws IOException {
        UrlDataSource source = DefaultDataSourceFactory.createUrlDataSource(Util.createUrl(mHost, mPort, URL));
        try {
            source.open(0);
            byte[] expectedResponse = RESPONSE.getBytes();
            byte[] actualResponse = new byte[expectedResponse.length];
            source.read(actualResponse);
            boolean isOk = Arrays.equals(expectedResponse, actualResponse);
            LogUtil.i("Echo is ok?" + isOk);
            return isOk;
        } catch (IOException e) {
            LogUtil.e("Error reading echo response", e);
            return false;
        } finally {
            source.close();
        }
    }

    private class RequestEchoCallable implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return requestEcho();
        }
    }

    public void response(Response response) throws IOException {
        byte[] headersBytes = mResponseEncoder.encode(response);
        response.write(headersBytes);
        byte[] bodyBytes = RESPONSE.getBytes();
        response.write(bodyBytes);
    }
}
