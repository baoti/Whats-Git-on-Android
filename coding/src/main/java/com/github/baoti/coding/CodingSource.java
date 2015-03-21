package com.github.baoti.coding;

import android.app.Activity;

import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.coding.api.Page;
import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

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
    public Observable<List<? extends Repository>> getRepositories(Activity activity, final int page, final int pageSize) {
        return sessionInterceptor.withSession(activity, api.listProjects(page, pageSize))
                .map(new Func1<CodingResponse<Page<CodingProject>>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(CodingResponse<Page<CodingProject>> pageCodingResponse) {
                        return pageCodingResponse.data.list;
                    }
                });
    }

    @Override
    public String toString() {
        return name();
    }
}
