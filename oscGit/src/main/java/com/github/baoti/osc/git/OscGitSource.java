package com.github.baoti.osc.git;

import com.github.baoti.git.GitSource;

/**
 * Created by liuyedong on 15-3-19.
 */
public class OscGitSource implements GitSource {
    @Override
    public String name() {
        return "Git@OSC";
    }

    @Override
    public String toString() {
        return name();
    }
}
