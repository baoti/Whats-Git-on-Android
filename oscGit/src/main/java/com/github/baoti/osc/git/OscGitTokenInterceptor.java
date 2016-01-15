package com.github.baoti.osc.git;

import android.app.Activity;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.RxUtils;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

public class OscGitTokenInterceptor extends AuthTokenProvider implements Interceptor {

    @Inject
    public OscGitTokenInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, OscGitConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        String token = getAuthToken();
        if (token != null) {
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter(OscGitConstants.QUERY_PARAM_TOKEN, token)
                    .build();
            request = request.newBuilder().url(url).build();
        }
        return chain.proceed(request);
    }

    public <T> Observable<T> withToken(Activity activity, Observable<T> request) {
        return RxUtils.afterDo(provideAuthToken(activity), request);
    }
}
