package com.github.baoti.github;

import com.github.baoti.git.Repository;

/**
 * Created by liuyedong on 15-3-19.
 */
public class GitHubRepository implements Repository {
    public String name;

    @Override
    public String toString() {
        return "GitHubRepository{" +
                "name='" + name + '\'' +
                '}';
    }
}
