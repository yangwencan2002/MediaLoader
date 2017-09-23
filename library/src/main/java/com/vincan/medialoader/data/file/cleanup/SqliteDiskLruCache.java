package com.vincan.medialoader.data.file.cleanup;

import com.vincan.medialoader.MediaLoaderConfig;

import java.io.File;

/**
 * 磁盘LRU缓存的sqlite实现
 * //TODO
 *
 * @author vincanyang
 */
public class SqliteDiskLruCache implements DiskLruCache {

    private final MediaLoaderConfig mMediaLoaderConfig;

    public SqliteDiskLruCache(MediaLoaderConfig mediaLoaderConfig) {
        mMediaLoaderConfig = mediaLoaderConfig;
    }

    @Override
    public void save(String url, File file) {

    }

    @Override
    public File get(String url) {
        return null;
    }

    @Override
    public void remove(String url) {

    }

    @Override
    public void close() {

    }

    @Override
    public void clear() {

    }
}
