package com.github.baoti.osc.git;

import com.github.baoti.git.Repository;

/**
 * Created by liuyedong on 15-3-19.
 */
public class OscGitProject implements Repository {
    public String name;

    @Override
    public String toString() {
        return "OscGitProject{" +
                "name='" + name + '\'' +
                '}';
    }
}
