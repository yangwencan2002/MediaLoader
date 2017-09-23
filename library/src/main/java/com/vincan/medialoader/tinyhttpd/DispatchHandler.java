package com.vincan.medialoader.tinyhttpd;

import com.vincan.medialoader.utils.LogUtil;
import com.vincan.medialoader.utils.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分发处理器
 *
 * @author vincanyang
 */
class DispatchHandler implements Runnable {

    private final Selector mSelector;

    private final ServerSocketChannel mServer;

    private volatile boolean mIsRunning = false;

    private TinyHttpd mHttpServer;

    private final CountDownLatch mThreadStartSignal;

    private ByteBuffer mRequestByteBuffer = ByteBuffer.allocateDirect(Util.DEFAULT_BUFFER_SIZE);//request最长为8192

    private final ExecutorService mServerExecutorService = Executors.newCachedThreadPool();

    public DispatchHandler(InetAddress address, int port, CountDownLatch startSignal, TinyHttpd httpServer) throws IOException {
        mSelector = Selector.open();
        mServer = ServerSocketChannel.open();
        mServer.socket().bind(new InetSocketAddress(address, port));
        mServer.configureBlocking(false);
        mServer.register(mSelector, SelectionKey.OP_ACCEPT);
        mHttpServer = httpServer;
        mThreadStartSignal = startSignal;
    }

    @Override
    public final void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);//避免和主线程抢占资源发生ANR
        mThreadStartSignal.countDown();
        mIsRunning = true;
        while (mIsRunning) {
            SelectionKey key = null;
            try {
                mSelector.select();
                Iterator<SelectionKey> keyIterator = mSelector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    key = keyIterator.next();
                    keyIterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        byte[] requestBytes = readRequestBytes(channel);
                        if (requestBytes != null) {
                            mServerExecutorService.submit(new IOHandler(channel, requestBytes, mHttpServer));
                        }
                    }
                }
            } catch (Exception e) {
                if (key != null) {
                    key.cancel();
                }
                LogUtil.e(e);
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(mSelector, SelectionKey.OP_READ);
    }

    private byte[] readRequestBytes(SocketChannel channel) throws IOException {
        int readCount;
        try {
            readCount = channel.read(mRequestByteBuffer);
        } catch (IOException e) {
            LogUtil.d("The client closed the connection", e);
            channel.close();
            throw e;
        }
        if (readCount < 0) {
            LogUtil.d("The client shut the socket down");
            channel.close();
            return null;
        }
        mRequestByteBuffer.flip();
        byte[] requestBytes = new byte[readCount];
        mRequestByteBuffer.get(requestBytes);
        mRequestByteBuffer.clear();
        return requestBytes;
    }

    public int getPort() {
        return mServer.socket().getLocalPort();
    }

    public final void destroy() {
        mIsRunning = false;
        try {
            mServer.close();
        } catch (IOException e) {
            LogUtil.e("error closing server", e);
        }
    }
}