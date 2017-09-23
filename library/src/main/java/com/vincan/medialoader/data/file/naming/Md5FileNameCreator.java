package com.vincan.medialoader.data.file.naming;

import android.text.TextUtils;

import com.vincan.medialoader.utils.Util;

/**
 * 使用图片URL地址的 MD5编码来进行生成缓存的文件名称
 *
 * @author vincanyang
 */
public class Md5FileNameCreator implements FileNameCreator {

    @Override
    public String create(String url) {
        String extension = Util.getExtensionFromUrl(url);
        String name = Util.getMD5(url);
        return TextUtils.isEmpty(extension) ? name : (name + "." + extension);
    }
}