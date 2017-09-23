package com.vincan.medialoader.data;

import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.data.file.FileDataSource;
import com.vincan.medialoader.data.file.RandomAcessFileDataSource;
import com.vincan.medialoader.data.file.cleanup.DiskLruCache;
import com.vincan.medialoader.data.file.cleanup.SimpleDiskLruCache;
import com.vincan.medialoader.data.url.DefaultUrlDataSource;
import com.vincan.medialoader.data.url.UrlDataSource;

import java.io.File;

/**
 * 数据源默认生产工厂
 *
 * @author vincanyang
 */
public final class DefaultDataSourceFactory {

    public static UrlDataSource createUrlDataSource(String url) {
        return new DefaultUrlDataSource(url);
    }

    public static UrlDataSource createUrlDataSource(DefaultUrlDataSource dataSource) {
        return new DefaultUrlDataSource(dataSource);
    }

    public static FileDataSource createFileDataSource(File file, DiskLruCache diskLruStorage) {
        return new RandomAcessFileDataSource(file, diskLruStorage);
    }

    public static DiskLruCache createDiskLruCache(MediaLoaderConfig mediaLoaderConfig) {
        return new SimpleDiskLruCache(mediaLoaderConfig);
    }
}
