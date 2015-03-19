package com.github.baoti.git;

import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface GitSource {
    String name();

    Observable<Repository> getRepositories(int page, int pageSize);
}
