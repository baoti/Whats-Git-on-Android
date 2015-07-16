package com.github.baoti.coding;

import android.app.Activity;

import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.RxUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class CodingSessionInterceptor extends AuthTokenProvider implements Interceptor {

    private static final String KEY_SESSION = "sid";

    @Inject
    public CodingSessionInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, CodingConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String sessionId = getAuthToken();
        if (sessionId != null) {
            request = request.newBuilder()
                    .addHeader("Cookie", KEY_SESSION + "=" + sessionId)
                    .build();
        }
        return chain.proceed(request);
    }

    public <T> Observable<T> withSession(Activity activity, Observable<T> request) {
        return RxUtils.afterDo(provideAuthToken(activity), request);
    }

    public static String fetchSessionId(retrofit.Response<CodingResponse<CodingUser>> response) throws NoSessionException {
        if (!CodingResponse.isSuccessful(response)) {
            throw new NoSessionException("No successful response");
        }
        String cookie = response.headers().get("Set-Cookie");
        if (cookie != null) {
            return fetchSessionId(HttpCookie.parse("Set-Cookie: " + cookie));
        }
        throw new NoSessionException("No Set-Cookie header");
    }

    private static String fetchSessionId(List<HttpCookie> cookies) throws NoSessionException {
        for (HttpCookie cookie : cookies) {
            if (KEY_SESSION.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new NoSessionException(String.format("No such cookie: %s", KEY_SESSION));
    }

    public static class NoSessionException extends Exception {

        public NoSessionException(String detailMessage) {
            super(detailMessage);
        }

    }
}
