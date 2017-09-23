package com.vincan.medialoader.sample;

/**
 * 配置类
 *
 * @author vincanyang
 */
public enum MediaConfig {
    PIG1(Config.DIR_ROOT + "pig1.mp4"),
    PIG2(Config.DIR_ROOT + "pig2.mp4"),
    PIG3(Config.DIR_ROOT + "pig3.mp4"),
    PIG4(Config.DIR_ROOT + "pig4.mp4"),
    CLOUD(Config.DIR_ROOT + "cloud.mp3");

    public final String url;

    MediaConfig(String url) {
        this.url = url;
    }

    private class Config {
        private static final String DIR_ROOT = "https://raw.githubusercontent.com/yangwencan2002/MediaLoader/master/mediafile/";
    }
}
