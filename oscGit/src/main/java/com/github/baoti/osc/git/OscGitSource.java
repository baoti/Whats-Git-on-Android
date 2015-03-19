package com.github.baoti.osc.git;

import android.accounts.Account;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.osc.git.api.OscGitApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;
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

    private OscGitApi apiWithoutToken;

    @Inject
    public OscGitSource(AccountUtils accountUtils, String accountType) {
        this.accountUtils = accountUtils;
        this.accountType = accountType;
        apiWithoutToken = restAdapterBuilder().build().create(OscGitApi.class);
    }

    @Override
    public String name() {
        return "Git@OSC";
    }

    @Override
    public Observable<Repository> getRepositories(final int page, final int pageSize) {
        return apiWithToken()
                .flatMap(new Func1<OscGitApi, Observable<OscGitProject>>() {
                    @Override
                    public Observable<OscGitProject> call(OscGitApi oscGitApi) {
                        return oscGitApi.listPopularProjects(page, pageSize);
                    }
                })
                .cast(Repository.class);
    }

    @Override
    public String toString() {
        return name();
    }

    private RestAdapter.Builder restAdapterBuilder() {
        return new RestAdapter.Builder()
                .setEndpoint(OscGitApi.API_URL);
    }

    private Observable<OscGitApi> apiWithToken() {
        return tokenRequestInterceptor()
                .map(new Func1<RequestInterceptor, OscGitApi>() {
                    @Override
                    public OscGitApi call(RequestInterceptor interceptor) {
                        return restAdapterBuilder()
                                .setRequestInterceptor(interceptor)
                                .build()
                                .create(OscGitApi.class);
                    }
                });
    }

    private Observable<String> getToken() {
        if (tokenRequest != null) {
            return tokenRequest;
        }
        tokenRequest = PublishSubject.create();
        accountUtils.getAccount(accountType, OscGitConstants.AUTH_TOKEN_TYPE)
                .flatMap(new Func1<Account, Observable<String>>() {
                    @Override
                    public Observable<String> call(Account account) {
                        return accountUtils.getAuthToken(account);
                    }
                })
                .subscribe(tokenRequest);
        return tokenRequest;
    }

    private Observable<RequestInterceptor> tokenRequestInterceptor() {
        return getToken().map(new Func1<String, RequestInterceptor>() {
            @Override
            public RequestInterceptor call(final String s) {
                return new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam(OscGitConstants.QUERY_PARAM_TOKEN, s);
                    }
                };
            }
        });
    }
}
