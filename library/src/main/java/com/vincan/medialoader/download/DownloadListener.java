package com.vincan.medialoader.download;

import java.io.File;

/**
 * 下载监听器
 *
 * @author vincanyang
 */
public interface DownloadListener {

    void onProgress(String url, File file, int progress);

    void onError(Throwable e);
}
