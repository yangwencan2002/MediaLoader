package com.vincan.medialoader.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.vincan.medialoader.tinyhttpd.HttpConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 通用工具类
 *
 * @author vincanyang
 */

public final class Util {

    public static final String LOCALHOST = "127.0.0.1";//不建议使用localhost，因为需要访问网络

    public static final int DEFAULT_BUFFER_SIZE = 8192;//8 * 1024

    public static final String CHARSET_DEFAULT = "UTF-8";

    private static final int MAX_EXTENSION_LENGTH = 4;

    private Util() {

    }

    public static <T> T notEmpty(T object) {
        if (object instanceof String) {
            if (TextUtils.isEmpty((String) object)) {
                throw new NullPointerException(object.getClass().getSimpleName() + " cann't be empty");
            }
        } else {
            if (object == null) {
                throw new NullPointerException(object.getClass().getSimpleName() + " cann't be null");
            }
        }
        return object;
    }

    public static String encode(String url) {
        try {
            return URLEncoder.encode(url, CHARSET_DEFAULT);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }

    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, CHARSET_DEFAULT);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Decoding not supported", e);
        }
    }

    public static String createUrl(String host, int port, String path) {
        return String.format(Locale.US, "http://%s:%d/%s", host, port, Util.encode(path));
    }

    public static String createUrl(String host, int port, String path, String secret) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = getHmacSha1(path + timestamp, secret);
        StringBuilder sb = new StringBuilder("http://%s:%d/%s");
        sb.append("?").append(HttpConstants.PARAM_SIGN).append("=%s");
        sb.append("&").append(HttpConstants.PARAM_TIMESTAMP).append("=%s");
        return String.format(Locale.US, sb.toString(), host, port, Util.encode(path), Util.encode(sign), Util.encode(timestamp));
    }

    public static String getHmacSha1(String s, String keyString) {
        String hmacSha1 = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes());
            hmacSha1 = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {

        }
        return hmacSha1;
    }

    public static String getMimeTypeFromUrl(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return TextUtils.isEmpty(extension) ? "" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getExtensionFromUrl(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
                url.substring(dotIndex + 1, url.length()) : "";
    }

    public static String getMD5(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(string.getBytes());
            return bytesToHexString(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));// 以十六进制(x)输出,2为指定的输出字段的宽度.如果位数小于2,则左端补0
        }
        return sb.toString();
    }
}
