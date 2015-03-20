package com.github.baoti.git.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liuyedong on 15-3-20.
 */
public class Texts {
    public static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /** null 安全版的 toString() */
    public static String str(CharSequence s) {
        return str(s, "");
    }

    /** null 安全 的 toString() */
    public static String str(Object o, String defVal) {
        return o == null ? defVal : o.toString();
    }

    public static String strOrThrow(Object o, String msg) {
        if (o == null) {
            throw new NullPointerException(msg);
        }
        return o.toString();
    }

    public static byte[] sha1(byte[]... bytes) {
        return digest("SHA-1", bytes);
    }

    public static byte[] md5(byte[]... bytes) {
        return digest("MD5", bytes);
    }

    public static byte[] digest(String algorithm, byte[]... bytes) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            for (byte[] item : bytes) {
                messageDigest.update(item);
            }
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("NO MD5");
        }
    }

    /**
     * The digits for every supported radix.
     */
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final char[] UPPER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 字节数组转十六进制字符串
     * <p/>
     * Copy from java.lang.IntegralToString.
     */
    public static String bytesToHexString(byte[] bytes, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0xf];
            buf[c++] = digits[b & 0xf];
        }
        return new String(buf);
    }

    public static String base64(String s) {
        byte[] sourceBytes;
        try {
            sourceBytes = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 NOT SUPPORTED");
        }
        return Base64.encodeToString(sourceBytes, Base64.DEFAULT);
    }
}
