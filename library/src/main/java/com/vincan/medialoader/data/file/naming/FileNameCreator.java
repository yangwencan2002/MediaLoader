package com.vincan.medialoader.data.file.naming;

/**
 * 文件名的命名规范接口
 *
 * @author vincanyang
 */
public interface FileNameCreator {

    /**
     * 根据url来进行生成特定的文件名
     */
    String create(String url);
}