# MediaLoader-音视频边下边播组件
[![Build Status](https://api.travis-ci.org/yangwencan2002/MediaLoader.svg?branch=master)](https://travis-ci.org/yangwencan2002/MediaLoader/) [ ![Download](https://api.bintray.com/packages/yangwencan2002/maven/MediaLoader/images/download.svg) ](https://bintray.com/yangwencan2002/maven/MediaLoader/_latestVersion)

## 简介
一行代码就能支持任意播放器的音/视频边下边播功能！MediaLoader是一个可应用于音/视频的边下边播、缓存管理和预下载等场景的音/视频加载组件。

## 功能特性
- 边下载边播放功能，无需等待下载完成后才播放；
- 下载缓存功能，再次播放会使用已有的缓存，不浪费额外流量；
- 支持Android所有主流播放器（如MediaPlayer、VideoView）、第三方播放器（如ExoPlayer、ijkplayer、腾讯视频）；
- 缓存自动管理功能，可以设置缓存路径、文件命名规则、最大缓存空间、最大缓存数量等；
- 预下载功能，可以预先下载需要的音视频，支持暂停、继续（断点续传）等操作。

## 快速上手
1、添加依赖：
```
dependencies {
    compile 'com.vincan:medialoader:1.0.0'
}
```
2、代码使用：
只要一行代码就能拥有强大功能，将音视频的原有URL替换成代理URL，然后像往常一样使用即可：
```
String proxyUrl = MediaLoader.getInstance(getContext()).getProxyUrl(VIDEO_URL);
videoView.setVideoPath(proxyUrl);
```

## 功能清单
#### 边下边播（MediaLoader）：

|功能|API|
|------|------|
| 创建实例| MediaLoader#getInstance(Context context)|
| 初始化设置| MediaLoader#init(MediaLoaderConfig mediaLoaderConfig)|
| 获取代理url| MediaLoader#getProxyUrl(String url)|
| 是否缓存文件| MediaLoader#isCached(String url)|
| 获取缓存的文件| MediaLoader#getCacheFile(String url)|
| 添加下载监听器| MediaLoader#addDownloadListener(String url, DownloadListener listener)|
| 删除下载监听器| MediaLoader#removeDownloadListener(String url, DownloadListener listener)|
| 删除下载监听器| MediaLoader#removeDownloadListener(DownloadListener listener)|
| 暂停下载| MediaLoader#pauseDownload(String url)|
| 继续下载| MediaLoader#resumeDownload(String url)|
| 销毁实例| MediaLoader#destroy()|

#### MediaLoader初始化设置（MediaLoaderConfig.Builder）：

|功能|API|
|------|------|
| 设置缓存目录| MediaLoaderConfig.Builder#cacheRootDir(File file)|
| 设置缓存文件命名生成器| MediaLoaderConfig.Builder#cacheFileNameGenerator(FileNameCreator fileNameCreator)|
| 设置最大缓存文件总大小| MediaLoaderConfig.Builder#maxCacheFilesSize(long size)|
| 设置最大缓存文件数量| MediaLoaderConfig.Builder#maxCacheFilesCount(int count)|
| 设置最大缓存文件时间期限| MediaLoaderConfig.Builder#maxCacheFileTimeLimit(long timeLimit)|
| 设置下载线程池数量| MediaLoaderConfig.Builder#downloadThreadPoolSize(int threadPoolSize)|
| 设置下载线程优先级| MediaLoaderConfig.Builder#downloadThreadPriority(int threadPriority)|
| 设置下载ExecutorService| MediaLoaderConfig.Builder#downloadExecutorService(ExecutorService executorService)|
| 创建MediaLoaderConfig实例| MediaLoaderConfig.Builder#build()|

#### 预下载（DownloadManager）：

|功能|API|
|------|------|
| 创建实例| DownloadManager#getInstance(Context context)|
| 启动下载| DownloadManager#enqueue(Request request)|
| 启动下载| DownloadManager#enqueue(Request request, DownloadListener listener)|
| 下载是否正在运行| DownloadManager#isRunning(String url)|
| 暂停下载| DownloadManager#pause(String url)|
| 继续下载| DownloadManager#resume(String url)|
| 停止下载| DownloadManager#stop(String url)|
| 暂停所有下载| DownloadManager#pauseAll()|
| 继续所有下载| DownloadManager#resumeAll()|
| 停止所有下载| DownloadManager#stopAll()|
| 是否缓存文件| DownloadManager#isCached(String url)|
| 获取缓存的文件| DownloadManager#getCacheFile(String url)|
| 清除缓存目录| DownloadManager#cleanCacheDir()|

## DEMO
DEMO请直接参见源码中的sample工程，它就几种常见的边下边播场景进行展示如何使用，如图所示：
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample.jpg)
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample2.jpg)

## FAQ常见问题
#### 1.MediaLoader的默认初始化配置是怎么样的？

|参数名|默认值|
|------|------|
|文件缓存目录|sdcard/Android/data/${application package}/cache/medialoader|
|文件命名规则|MD5(url)|
|最大缓存文件数|500|
|最大缓存空间|500* 1024 * 1024（500M）|
|最大缓存期限|10 * 24 * 60 * 60（10天）|
|下载线程数|3|
|下载线程优先级|Thread.MAX_PRIORITY|

#### 2.如何更改默认初始化配置？
默认的配置参数相对来说已经比较合理，如果不满足需求可以通过以下代码进行更改：
```
        MediaLoaderConfig mediaLoaderConfig = new MediaLoaderConfig.Builder(this)
                .cacheRootDir(DefaultConfigFactory.createCacheRootDir(this, "my_cache_dir"))
                .cacheFileNameGenerator(new HashCodeFileNameCreator())//组件内置的hash code命名规则
                .maxCacheFilesCount(100)
                .maxCacheFilesSize(100 * 1024 * 1024)
                .maxCacheFileTimeLimit(5 * 24 * 60 * 60)
                .downloadThreadPoolSize(3)
                .downloadThreadPriority(Thread.NORM_PRIORITY)
                .build();
        MediaLoader.getInstance(this).init(mediaLoaderConfig);
```

## 版本发布
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
