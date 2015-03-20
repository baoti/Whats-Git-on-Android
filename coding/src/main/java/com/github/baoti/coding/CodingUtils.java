package com.github.baoti.coding;

import static com.github.baoti.git.util.Texts.ISO_8859_1;
import static com.github.baoti.git.util.Texts.bytesToHexString;
import static com.github.baoti.git.util.Texts.sha1;

/**
 * Created by liuyedong on 15-3-20.
 */
public class CodingUtils {

    /**
     * 得到编码后的密码
     */
    public static String passwordSha1(String password) {
        return bytesToHexString(sha1(password.getBytes(ISO_8859_1)), false);
    }

}
