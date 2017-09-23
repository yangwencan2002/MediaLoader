package com.vincan.medialoader.data.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * 文件数据源接口，
 * <br>
 * 注意：读和写会在不同线程访问，需要支持多线程
 *
 * @author vincanyang
 */
public interface FileDataSource extends Closeable {

    long length() throws IOException;

    int seekAndRead(long offset, byte[] buffer) throws IOException;

    void append(byte[] data, int length) throws IOException;

    void complete() throws IOException;

    boolean isCompleted();

    File getFile();
}
