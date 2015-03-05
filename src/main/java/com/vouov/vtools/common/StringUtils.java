package com.vouov.vtools.common;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 字符操作工具类
 *
 * @author yuml
 * @date 2015/3/4
 */
public class StringUtils {

    private static byte[] getBytes(String string, Charset charset) {
        return string == null?null:string.getBytes(charset);
    }

    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if(string == null) {
            return null;
        } else {
            try {
                return string.getBytes(charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Charset.forName("UTF-8"));
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    private static String newString(byte[] bytes, Charset charset) {
        return bytes == null?null:new String(bytes, charset);
    }

    public static String newString(byte[] bytes, String charsetName) {
        if(bytes == null) {
            return null;
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes,  Charset.forName("UTF-8"));
    }

    public static byte[] getBytesIso8859_1(final String string) {
        return getBytes(string, Charset.forName("ISO_8859_1"));
    }

    public static String newStringIso8859_1(final byte[] bytes) {
        return new String(bytes, Charset.forName("ISO_8859_1"));
    }
}
