package com.vincan.medialoader.data.file.cleanup;

import java.io.File;

/**
 * 磁盘LRU缓存，使用LRU对磁盘文件进行清理
 *
 * @author vincanyang
 */
public interface DiskLruCache {

    void save(String url, File file);

    File get(String url);

    void remove(String url);

    void close();

    void clear();
}
