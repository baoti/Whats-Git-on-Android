package com.github.baoti.github;

import android.app.Activity;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.RxUtils;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

import static com.github.baoti.git.util.Texts.basicAuthorization;

public class GitHubTokenInterceptor extends AuthTokenProvider implements Interceptor {

    @Inject
    public GitHubTokenInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, GitHubConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder()
                .addHeader("User-Agent", "Whats-Git-on-Android")
                .addHeader("Accept", "application/vnd.github.v3+json");

        String token = getAuthToken();
        if (token != null) {
            requestBuilder.addHeader("Authorization", basicAuthorization(token, ""));
        }
        return chain.proceed(requestBuilder.build());
    }

    public <T> Observable<T> withToken(Activity activity, Observable<T> request) {
        return RxUtils.afterDo(provideAuthToken(activity), request);
    }
}
