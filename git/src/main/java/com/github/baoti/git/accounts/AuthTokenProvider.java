package com.github.baoti.git.accounts;

import android.app.Activity;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;
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

    /**
     * 准备 authToken, 妥当后 onCompleted, 失败时 onError。
     */
    protected <T> Observable<T> prepareAuthToken(Activity activity) {
        return provideAuthToken(activity)
                .ignoreElements()
                // 为了让编译器通过
                .map(AuthTokenProvider.<T>toNull());
    }

    private static <T> Func1<String, T> toNull() {
        return UtilityFunctions.returnNull();
    }
}
