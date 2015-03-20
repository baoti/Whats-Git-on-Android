package com.github.baoti.github;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;

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
import rx.subjects.PublishSubject;

/**
 * Created by liuyedong on 15-3-19.
 */
@Singleton
public class GitHubSource implements GitSource {

    private final AccountUtils accountUtils;
    private final String accountType;

    private PublishSubject<String> tokenRequest;

    private final GitHubTokenInterceptor tokenInterceptor;
    private final GitHubApi api;

    @Inject
    public GitHubSource(AccountUtils accountUtils, @AccountType String accountType) {
        this.accountUtils = accountUtils;
        this.accountType = accountType;
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
        return apiWithToken(activity)
                .flatMap(new Func1<GitHubApi, Observable<List<GitHubRepository>>>() {
                    @Override
                    public Observable<List<GitHubRepository>> call(GitHubApi gitHubApi) {
                        return gitHubApi.listRepositories();
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

    private Observable<GitHubApi> apiWithToken(Activity activity) {
        return getToken(activity)
                .map(new Func1<String, GitHubApi>() {
                    @Override
                    public GitHubApi call(String s) {
                        tokenInterceptor.setToken(s);
                        return api;
                    }
                });
    }

    private Observable<String> getToken(Activity activity) {
        if (tokenRequest != null) {
            if (!tokenRequest.hasThrowable()) {
                return tokenRequest;
            }
        }
        tokenRequest = PublishSubject.create();
        accountUtils.getAuthToken(activity, accountType, GitHubConstants.AUTH_TOKEN_TYPE)
                .map(new Func1<Bundle, String>() {
                    @Override
                    public String call(Bundle bundle) {
                        return bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    }
                })
                .subscribe(tokenRequest);
        return tokenRequest;
    }
}
