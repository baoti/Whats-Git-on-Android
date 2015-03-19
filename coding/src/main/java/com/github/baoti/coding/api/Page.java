package com.github.baoti.coding.api;

import java.util.List;

/**
 * Created by liuyedong on 15-3-19.
 */
public class Page<T> {
    public List<T> list;
    public int page;
    public int pageSize;
    public int totalPage;
    public int totalRow;
}
