![logo](https://github.com/yangwencan2002/MediaLoader/blob/master/logo.png)
# MediaLoader-音视频边下边播组件
[![Build Status](https://api.travis-ci.org/yangwencan2002/MediaLoader.svg?branch=master)](https://travis-ci.org/yangwencan2002/MediaLoader/)
[![Download](https://api.bintray.com/packages/yangwencan2002/maven/MediaLoader/images/download.svg) ](https://bintray.com/yangwencan2002/maven/MediaLoader/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## 目录
- [简介](#简介)
- [功能特性](#功能特性)
- [快速上手](#快速上手)
- [使用说明](#使用说明)
  - [监听下载状态](#监听下载状态)
  - [更改初始配置](#更改初始配置)
  - [预下载](#预下载)
- [文档](#文档)
- [DEMO](#demo)
- [FAQ](#faq)
- [更新日志](#更新日志)
- [版本发布](#版本发布)
- [支持帮助](#支持帮助)
- [License](#license)

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

## 使用说明

#### 监听下载状态
添加监听回调：

`MediaLoader.addDownloadListener(String url, DownloadListener listener)`

不要忘记删除回调以免内存泄漏：

`MediaLoader.removeDownloadListener(String url, DownloadListener listener)`

#### 更改初始配置
你可以通过`MediaLoaderConfig`更改默认的初始配置：
```java
        MediaLoaderConfig mediaLoaderConfig = new MediaLoaderConfig.Builder(this)
                .cacheRootDir(DefaultConfigFactory.createCacheRootDir(this, "your_cache_dir"))//directory for cached files
                .cacheFileNameGenerator(new HashCodeFileNameCreator())//names for cached files
                .maxCacheFilesCount(100)//max files count
                .maxCacheFilesSize(100 * 1024 * 1024)//max files size
                .maxCacheFileTimeLimit(5 * 24 * 60 * 60)//max file time
                .downloadThreadPoolSize(3)//download thread size
                .downloadThreadPriority(Thread.NORM_PRIORITY)//download thread priority
                .build();
        MediaLoader.getInstance(this).init(mediaLoaderConfig);
```

#### 预下载
在弱网络环境下，边下边播的体验并不太好，所以预下载变成边下边播的一个好的补充，事先就把音/视频下载到本地，避免用户长时间等待。
使用`DownloadManager.enqueue(Request request, DownloadListener listener)`开始预下载，`DownloadManager.stop(String url)`停止预下载。
`DownloadManager`还提供了其他方法如暂停、继续下载等便捷的方法。

更多细节请参考[功能清单](#功能清单)。

## 文档
#### 边下边播（MediaLoader）：

|描述|API|
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

|描述|API|
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

|描述|API|
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
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample.zh_cn.jpg)
![image](https://github.com/yangwencan2002/MediaLoader/blob/master/sample2.zh_cn.jpg)

## FAQ
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

## 更新日志
[release notes](https://github.com/yangwencan2002/MediaLoader/releases)

## 版本发布
[bintray.com](https://bintray.com/yangwencan2002/maven/MediaLoader)

## 支持帮助
1. 阅读[sample](https://github.com/yangwencan2002/MediaLoader/tree/master/sample)
2. 阅读[源码](https://github.com/yangwencan2002/MediaLoader/tree/master)
3. 创建[issue](https://github.com/yangwencan2002/MediaLoader/issues)
4. 联系我们

## License
`MediaLoader`是基于Apache-2.0许可证。详细请看[LICENSE](https://github.com/yangwencan2002/MediaLoader/blob/master/LICENSE)。
