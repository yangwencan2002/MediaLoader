# MediaLoader
[简体中文README](./README.zh_cn.md)
## Table of Content
- [Introduction](#introduction)
- [Features](#features)
- [Quick start](#quick-start)
- [Manual](#manual)
- [Sample](#sample)
- [FAQ](#faq)
- [Where released?](#where-released)
- [License](#license)

## Introduction
`MediaLoader` allow you to enable cache video/audio while playing for any android media player with single line code.

## Features
- caching to disk while streaming,no wait;
- offline work with cached data,no download again;
- working with any media player on android(MediaPlayer,VideoView,ExoPlayer,ijkplayer...);
- cache management(cache dir change,cache file rename,max cache files size limit, max cache files count limit...);
- pre download.

## Quick start
Just add dependency (`MediaLoader` was released in jcenter):
```
dependencies {
    compile 'com.vincan:medialoader:1.0.0'
}
```

and use new url from `MediaLoader` instead of original url:

```java
String proxyUrl = MediaLoader.getInstance(getContext()).getProxyUrl(VIDEO_URL);
videoView.setVideoPath(proxyUrl);
```

## Manual
#### MediaLoader

|function|API|
|------|------|
| get MediaLoader instance| MediaLoader#getInstance(Context context)|
| initialize MediaLoader| MediaLoader#init(MediaLoaderConfig mediaLoaderConfig)|
| get proxy url| MediaLoader#getProxyUrl(String url)|
| is file cached| MediaLoader#isCached(String url)|
| get cache file| MediaLoader#getCacheFile(String url)|
| add download listener| MediaLoader#addDownloadListener(String url, DownloadListener listener)|
| remove download listener| MediaLoader#removeDownloadListener(String url, DownloadListener listener)|
| remove download listener| MediaLoader#removeDownloadListener(DownloadListener listener)|
| pause download| MediaLoader#pauseDownload(String url)|
| resume download| MediaLoader#resumeDownload(String url)|
| destroy MediaLoader instance| MediaLoader#destroy()|

#### MediaLoaderConfig.Builder

|function|API|
|------|------|
| set cache root dir| MediaLoaderConfig.Builder#cacheRootDir(File file)|
| set cache file name generator| MediaLoaderConfig.Builder#cacheFileNameGenerator(FileNameCreator fileNameCreator)|
| set max cache files size| MediaLoaderConfig.Builder#maxCacheFilesSize(long size)|
| set max cache files count| MediaLoaderConfig.Builder#maxCacheFilesCount(int count)|
| set max cache file time| MediaLoaderConfig.Builder#maxCacheFileTimeLimit(long timeLimit)|
| set download thread pool size| MediaLoaderConfig.Builder#downloadThreadPoolSize(int threadPoolSize)|
| set download thread priority| MediaLoaderConfig.Builder#downloadThreadPriority(int threadPriority)|
| set download ExecutorService| MediaLoaderConfig.Builder#downloadExecutorService(ExecutorService executorService)|
| new MediaLoaderConfig instance| MediaLoaderConfig.Builder#build()|

#### DownloadManager

|function|API|
|------|------|
| get MediaLoader instance| DownloadManager#getInstance(Context context)|
| start download| DownloadManager#enqueue(Request request)|
| start download| DownloadManager#enqueue(Request request, DownloadListener listener)|
| is download task running| DownloadManager#isRunning(String url)|
| pause download| DownloadManager#pause(String url)|
| resume download| DownloadManager#resume(String url)|
| stop download| DownloadManager#stop(String url)|
| pause all download| DownloadManager#pauseAll()|
| resume all download| DownloadManager#resumeAll()|
| stop all download| DownloadManager#stopAll()|
| is file cached| DownloadManager#isCached(String url)|
| get cache file| DownloadManager#getCacheFile(String url)|
| clean cache dir| DownloadManager#cleanCacheDir()|

## Sample
See `sample` project.<br>
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample.jpg)
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample2.jpg)

## FAQ
#### 1.What is the default initialization configuration for MediaLoader?

|config key|default value|
|------|------|
|cache dir|sdcard/Android/data/${application package}/cache/medialoader|
|cache file naming|MD5(url)|
|max cache files count|500|
|max cache files size|500* 1024 * 1024（500M）|
|max cache file time|10 * 24 * 60 * 60（10 days）|
|download thread pool size|3|
|download thread priority|Thread.MAX_PRIORITY|

#### 2.How to change the default initialization configuration?
```java
        MediaLoaderConfig mediaLoaderConfig = new MediaLoaderConfig.Builder(this)
                .cacheRootDir(DefaultConfigFactory.createCacheRootDir(this, "my_cache_dir"))
                .cacheFileNameGenerator(new HashCodeFileNameCreator())
                .maxCacheFilesCount(100)
                .maxCacheFilesSize(100 * 1024 * 1024)
                .maxCacheFileTimeLimit(5 * 24 * 60 * 60)
                .downloadThreadPoolSize(3)
                .downloadThreadPriority(Thread.NORM_PRIORITY)
                .build();
        MediaLoader.getInstance(this).init(mediaLoaderConfig);
```

## Where released?
[bintray.com](https://bintray.com/yangwencan2002/maven/MediaLoader)

## License

    Copyright 2017 Vincan Yang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.