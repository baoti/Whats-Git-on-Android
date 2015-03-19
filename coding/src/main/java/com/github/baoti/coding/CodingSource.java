package com.github.baoti.coding;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;

import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public class CodingSource implements GitSource {

    @Override
    public String name() {
        return "Coding";
    }

    @Override
    public Observable<Repository> getRepositories(int page, int pageSize) {
        return Observable.empty();
    }

    @Override
    public String toString() {
        return name();
    }
}
