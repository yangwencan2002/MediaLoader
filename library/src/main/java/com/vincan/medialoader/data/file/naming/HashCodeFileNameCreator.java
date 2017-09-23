package com.vincan.medialoader.data.file.naming;

/**
 * 使用视频的URL地址的HashCode编码作为文本名称
 *
 * @author vincanyang
 */
public class HashCodeFileNameCreator implements FileNameCreator {
    @Override
    public String create(String url) {
        return String.valueOf(url.hashCode());
    }
}