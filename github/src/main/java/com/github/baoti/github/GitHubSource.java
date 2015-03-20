package com.github.baoti.github;

import android.app.Activity;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.github.api.GitHubApi;

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
public class GitHubSource implements GitSource {

    private final AccountUtils accountUtils;

    private final GitHubTokenInterceptor tokenInterceptor;
    private final GitHubApi api;

    @Inject
    public GitHubSource(AccountUtils accountUtils) {
        this.accountUtils = accountUtils;
        this.tokenInterceptor = new GitHubTokenInterceptor();
        api = new RestAdapter.Builder()
                .setEndpoint(GitHubApi.API_URL)
                .setRequestInterceptor(tokenInterceptor)
                .build()
                .create(GitHubApi.class);
    }

    @Override
    public String name() {
        return "GitHub";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, final int page, final int pageSize) {
        return tokenInterceptor.withToken(activity, accountUtils)
                .flatMap(new Func1<String, Observable<List<GitHubRepository>>>() {
                    @Override
                    public Observable<List<GitHubRepository>> call(String token) {
                        return api.listRepositories();
                    }
                })
                .map(new Func1<List<GitHubRepository>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(List<GitHubRepository> gitHubRepositories) {
                        return gitHubRepositories;
                    }
                });
    }

    @Override
    public String toString() {
        return name();
    }
}
