package com.vincan.medialoader.tinyhttpd;

import android.text.TextUtils;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http头部
 *
 * @author vincanyang
 */
public class HttpHeaders extends LinkedHashMap<String, String> {

    private static final Pattern HEADER_RANGE_PATTERN = Pattern.compile("bytes=(\\d*)-");

    public interface Names {
        String RANGE = "Range";
        String ACCEPT_RANGES = "Accept-Ranges";
        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_RANGE = "Content-Range";
        String CONTENT_TYPE = "Content-Type";
    }

    public interface Values {
        String BYTES = "bytes";
    }

    private long mRangeOffset = Long.MIN_VALUE;

    public long getRangeOffset() {
        if (mRangeOffset == Long.MIN_VALUE) {
            String range = get(HttpHeaders.Names.RANGE);
            if (!TextUtils.isEmpty(range)) {
                Matcher matcher = HEADER_RANGE_PATTERN.matcher(range);
                if (matcher.find()) {
                    String rangeValue = matcher.group(1);
                    mRangeOffset = Long.parseLong(rangeValue);
                }
            }
        }
        return Math.max(mRangeOffset, 0);
    }

    public boolean isPartial() {
        return containsKey(HttpHeaders.Names.RANGE);
    }
}
