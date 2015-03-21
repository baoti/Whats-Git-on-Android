package com.github.baoti.github;

import android.app.Activity;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.RxUtils;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import rx.Observable;

import static com.github.baoti.git.util.Texts.basicAuthorization;

public class GitHubTokenInterceptor extends AuthTokenProvider implements RequestInterceptor {

    @Inject
    public GitHubTokenInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, GitHubConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "Whats-Git-on-Android");
        request.addHeader("Accept", "application/vnd.github.v3+json");

        String token = getAuthToken();
        if (token != null) {
            request.addHeader("Authorization", basicAuthorization(token, ""));
        }
    }

    public <T> Observable<T> withToken(Activity activity, Observable<T> request) {
        return RxUtils.afterDo(provideAuthToken(activity), request);
    }
}
