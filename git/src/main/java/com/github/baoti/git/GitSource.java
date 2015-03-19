package com.github.baoti.git;

import android.app.Activity;

import java.util.List;

import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface GitSource {
    String name();

    Observable<List<? extends Repository>> getRepositories(Activity activity, int page, int pageSize);
}
