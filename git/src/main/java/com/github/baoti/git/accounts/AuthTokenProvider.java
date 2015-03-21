package com.github.baoti.git.accounts;

import android.app.Activity;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;

/**
 * 用于单一类型的 authToken 工厂与缓存
 */
public class AuthTokenProvider {

    private final AccountUtils accountUtils;
    private final String accountType;
    private final String authTokenType;

    public AuthTokenProvider(AccountUtils accountUtils, String accountType, String authTokenType) {
        this.accountUtils = accountUtils;
        this.accountType = accountType;
        this.authTokenType = authTokenType;
    }

    private String authToken;

    private AsyncSubject<String> authTokenRequest;

    void saveAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    protected Observable<String> provideAuthToken(Activity activity) {
        if (authTokenRequest == null || authTokenRequest.hasThrowable()) {
            authTokenRequest = AsyncSubject.create();
            accountUtils.getAuthToken(activity, accountType, authTokenType)
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            saveAuthToken(s);
                        }
                    })
                    .subscribe(authTokenRequest);
        }
        return authTokenRequest;
    }
}
