package com.github.baoti.coding.api;

import com.github.baoti.coding.CodingUser;

import java.util.Map;

/**
 * Created by liuyedong on 15-3-19.
 */
public class CodingResponse<D> {
    public int code;
    public D data;
    public Map<String, String> msg;

    public static boolean isSuccessful(retrofit2.Response<CodingResponse<CodingUser>> response) {
        return response.isSuccessful() && response.body().code == 0;
    }
}
