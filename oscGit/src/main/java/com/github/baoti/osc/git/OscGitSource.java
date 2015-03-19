package com.github.baoti.osc.git;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;

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
import rx.subjects.PublishSubject;

/**
 * Created by liuyedong on 15-3-19.
 */
@Singleton
public class OscGitSource implements GitSource {

    private final AccountUtils accountUtils;
    private final String accountType;

    private PublishSubject<String> tokenRequest;

    private final OscGitTokenInterceptor tokenInterceptor;
    private final OscGitApi api;

    @Inject
    public OscGitSource(AccountUtils accountUtils, @AccountType String accountType) {
        this.accountUtils = accountUtils;
        this.accountType = accountType;
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
        return apiWithToken(activity)
                .flatMap(new Func1<OscGitApi, Observable<List<OscGitProject>>>() {
                    @Override
                    public Observable<List<OscGitProject>> call(OscGitApi oscGitApi) {
                        return oscGitApi.listPopularProjects(page, pageSize);
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

    private Observable<OscGitApi> apiWithToken(Activity activity) {
        return getToken(activity)
                .map(new Func1<String, OscGitApi>() {
                    @Override
                    public OscGitApi call(String s) {
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
        accountUtils.getAuthToken(activity, accountType, OscGitConstants.AUTH_TOKEN_TYPE)
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
