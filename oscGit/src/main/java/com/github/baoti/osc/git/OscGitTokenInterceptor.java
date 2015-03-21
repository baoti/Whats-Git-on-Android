package com.github.baoti.osc.git;

import android.app.Activity;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.RxUtils;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import rx.Observable;

public class OscGitTokenInterceptor extends AuthTokenProvider implements RequestInterceptor {

    @Inject
    public OscGitTokenInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, OscGitConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public void intercept(RequestFacade request) {
        String token = getAuthToken();
        if (token != null) {
            request.addQueryParam(OscGitConstants.QUERY_PARAM_TOKEN, token);
        }
    }

    public <T> Observable<T> withToken(Activity activity, Observable<T> request) {
        return RxUtils.afterDo(provideAuthToken(activity), request);
    }
}
