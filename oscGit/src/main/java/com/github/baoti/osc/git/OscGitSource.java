package com.github.baoti.osc.git;

import android.app.Activity;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.osc.git.api.OscGitApi;

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
public class OscGitSource implements GitSource {

    private final AccountUtils accountUtils;

    private final OscGitTokenInterceptor tokenInterceptor;
    private final OscGitApi api;

    @Inject
    public OscGitSource(AccountUtils accountUtils) {
        this.accountUtils = accountUtils;
        this.tokenInterceptor = new OscGitTokenInterceptor();
        api = new RestAdapter.Builder()
                .setEndpoint(OscGitApi.API_URL)
                .setRequestInterceptor(tokenInterceptor)
                .build()
                .create(OscGitApi.class);
    }

    @Override
    public String name() {
        return "Git@OSC";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, final int page, final int pageSize) {
        return tokenInterceptor.withToken(activity, accountUtils)
                .flatMap(new Func1<String, Observable<List<OscGitProject>>>() {
                    @Override
                    public Observable<List<OscGitProject>> call(String token) {
                        return api.listPopularProjects(page, pageSize);
                    }
                })
                .map(new Func1<List<OscGitProject>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(List<OscGitProject> oscGitProjects) {
                        return oscGitProjects;
                    }
                });
    }

    @Override
    public String toString() {
        return name();
    }
}
