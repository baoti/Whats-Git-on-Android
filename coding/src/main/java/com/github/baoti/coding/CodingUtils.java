package com.github.baoti.coding;

import java.nio.charset.Charset;

import static com.github.baoti.git.util.Texts.bytesToHexString;
import static com.github.baoti.git.util.Texts.digest;

/**
 * Created by liuyedong on 15-3-20.
 */
public class CodingUtils {
    static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");

    /**
     * 得到编码后的密码
     */
    public static String passwordSha1(String password) {
        return bytesToHexString(digest("SHA-1", password.getBytes(ISO_8859_1)), false);
    }

}
