package com.github.baoti.coding;

import android.app.Activity;

import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.coding.api.Page;
import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.util.RxUtils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by liuyedong on 15-3-19.
 */
@Singleton
public class CodingSource implements GitSource {

    private final CodingSessionInterceptor sessionInterceptor;
    private final CodingApi api;

    @Inject
    public CodingSource(CodingSessionInterceptor interceptor) {
        sessionInterceptor = interceptor;
        api = new RestAdapter.Builder()
                .setEndpoint(CodingApi.API_URL)
                .setRequestInterceptor(interceptor)
                .build()
                .create(CodingApi.class);
    }

    @Override
    public String name() {
        return "Coding";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, PublishSubject<?> nextPageTrigger) {
        return sessionInterceptor.withSession(activity, listProjects(20, nextPageTrigger))
                .map(new Func1<CodingResponse<Page<CodingProject>>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(CodingResponse<Page<CodingProject>> pageCodingResponse) {
                        return pageCodingResponse.data.list;
                    }
                });
    }

    private Observable<CodingResponse<Page<CodingProject>>> listProjects(final int pageSize, final PublishSubject<?> nextPageTrigger) {
        return api.listProjects(1, pageSize).concatWith(
                Observable.range(2, Integer.MAX_VALUE - 2)
                        .concatMap(new Func1<Integer, Observable<? extends CodingResponse<Page<CodingProject>>>>() {
                            @Override
                            public Observable<? extends CodingResponse<Page<CodingProject>>> call(Integer integer) {
                                return RxUtils.afterDo(nextPageTrigger.limit(1),
                                        api.listProjects(integer, pageSize));
                            }
                        }));
    }

    @Override
    public String toString() {
        return name();
    }
}
