package com.github.baoti.osc.git;

import android.app.Activity;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.util.RxUtils;
import com.github.baoti.osc.git.api.OscGitApi;

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
public class OscGitSource implements GitSource {

    private final OscGitTokenInterceptor tokenInterceptor;
    private final OscGitApi api;

    @Inject
    public OscGitSource(OscGitTokenInterceptor interceptor) {
        this.tokenInterceptor = interceptor;
        api = new RestAdapter.Builder()
                .setEndpoint(OscGitApi.API_URL)
                .setRequestInterceptor(interceptor)
                .build()
                .create(OscGitApi.class);
    }

    @Override
    public String name() {
        return "Git@OSC";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, PublishSubject<?> nextPageTrigger) {
        return tokenInterceptor.withToken(activity, listProjects(20, nextPageTrigger))
                .map(new Func1<List<OscGitProject>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(List<OscGitProject> oscGitProjects) {
                        return oscGitProjects;
                    }
                });
    }

    private Observable<List<OscGitProject>> listProjects(final int pageSize, final PublishSubject<?> nextPageTrigger) {
        return api.listPopularProjects(1, pageSize).concatWith(
                Observable.range(2, Integer.MAX_VALUE - 2)
                        .concatMap(new Func1<Integer, Observable<? extends List<OscGitProject>>>() {
                            @Override
                            public Observable<? extends List<OscGitProject>> call(Integer integer) {
                                return RxUtils.afterDo(nextPageTrigger.limit(1),
                                        api.listPopularProjects(integer, pageSize));
                            }
                        }));
    }

    @Override
    public String toString() {
        return name();
    }
}
