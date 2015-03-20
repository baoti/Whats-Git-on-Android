package com.github.baoti.git.util;

/**
 * Created by liuyedong on 15-3-20.
 */
public class Contracts {

    public static <T> T notNull(T v, String msg) {
        if (v == null) {
            throw new NullPointerException(msg);
        }
        return v;
    }
}
