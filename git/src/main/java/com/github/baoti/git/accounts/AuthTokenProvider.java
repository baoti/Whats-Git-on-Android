package com.github.baoti.git.accounts;

import android.app.Activity;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;

/**
 * 用于单一类型的 authToken 工厂与缓存
 */
public class AuthTokenProvider {

    private String authToken;

    private AsyncSubject<String> authTokenRequest;

    public void saveAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    protected Observable<String> provideAuthToken(
            Activity activity, AccountUtils accountUtils, String accountType, String authTokenType) {
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
