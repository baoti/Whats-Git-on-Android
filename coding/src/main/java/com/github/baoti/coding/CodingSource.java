package com.github.baoti.coding;

import com.github.baoti.git.GitSource;

/**
 * Created by liuyedong on 15-3-19.
 */
public class CodingSource implements GitSource {
    @Override
    public String name() {
        return "Coding";
    }

    @Override
    public String toString() {
        return name();
    }
}
