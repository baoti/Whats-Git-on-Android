package com.github.baoti.coding.api;

import java.util.Map;

/**
 * Created by liuyedong on 15-3-19.
 */
public class CodingResponse<D> {
    public int code;
    public D data;
    public Map<String, String> msg;
}
