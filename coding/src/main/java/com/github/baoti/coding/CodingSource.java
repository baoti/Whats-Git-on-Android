package com.github.baoti.coding;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;

import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.coding.api.Page;
import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.accounts.AccountUtils;

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

    private final AccountUtils accountUtils;
    private final String accountType;

    private PublishSubject<String> tokenRequest;

    private final CodingSessionInterceptor sessionInterceptor;
    private final CodingApi api;

    @Inject
    public CodingSource(AccountUtils accountUtils, @AccountType String accountType) {
        this.accountUtils = accountUtils;
        this.accountType = accountType;
        sessionInterceptor = new CodingSessionInterceptor();
        api = new RestAdapter.Builder()
                .setEndpoint(CodingApi.API_URL)
                .setRequestInterceptor(sessionInterceptor)
                .build()
                .create(CodingApi.class);
    }

    @Override
    public String name() {
        return "Coding";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, int page, int pageSize) {
        return api.listProjects(page, pageSize)
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

    private Observable<CodingApi> apiWithToken(Activity activity) {
        return getToken(activity)
                .map(new Func1<String, CodingApi>() {
                    @Override
                    public CodingApi call(String s) {
                        sessionInterceptor.setSessionId(s);
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
        accountUtils.getAuthToken(activity, accountType, CodingConstants.AUTH_TOKEN_TYPE)
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
