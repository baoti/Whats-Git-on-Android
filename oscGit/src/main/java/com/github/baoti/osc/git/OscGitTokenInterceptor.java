package com.github.baoti.osc.git;

import android.app.Activity;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;

import retrofit.RequestInterceptor;
import rx.Observable;

public class OscGitTokenInterceptor extends AuthTokenProvider implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        String token = getAuthToken();
        if (token != null) {
            request.addQueryParam(OscGitConstants.QUERY_PARAM_TOKEN, token);
        }
    }

    public Observable<String> withToken(Activity activity, AccountUtils accountUtils) {
        return provideAuthToken(activity, accountUtils,
                activity.getString(OscGitConstants.ACCOUNT_TYPE_RES),
                OscGitConstants.AUTH_TOKEN_TYPE);
    }
}
