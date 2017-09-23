package com.vincan.medialoader.data.url;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link UrlDataSource}的基础实现
 *
 * @author vincanyang
 */
public abstract class BaseUrlDataSource implements UrlDataSource {

    /**
     * 用于缓存url相关信息，避免反复http请求
     */
    protected Map<String, UrlDataSourceInfo> mUrlDataSourceInfoCache = new HashMap<>();

}
