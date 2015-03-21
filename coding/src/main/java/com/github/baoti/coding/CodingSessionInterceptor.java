package com.github.baoti.coding;

import android.app.Activity;

import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;

import java.net.HttpCookie;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Observable;

@Singleton
public class CodingSessionInterceptor extends AuthTokenProvider implements RequestInterceptor {

    private static final String KEY_SESSION = "sid";

    @Inject
    public CodingSessionInterceptor(AccountUtils accountUtils, @AccountType String accountType) {
        super(accountUtils, accountType, CodingConstants.AUTH_TOKEN_TYPE);
    }

    @Override
    public void intercept(RequestFacade request) {
        String sessionId = getAuthToken();
        if (sessionId != null) {
            request.addHeader("Cookie", KEY_SESSION + "=" + sessionId);
        }
    }

    public <T> Observable<T> withSession(Activity activity, Observable<T> request) {
        return this.<T>prepareAuthToken(activity).concatWith(request);
    }

    public static String fetchSessionId(Response response) throws NoSessionException {
        if (!CodingResponse.isSuccessful(response)) {
            throw new NoSessionException("No successful response");
        }
        List<Header> headers = response.getHeaders();
        for (Header header : headers) {
            if (header.getName().startsWith("Set-Cookie")) {
                return fetchSessionId(HttpCookie.parse(
                        header.getName() + ": " + header.getValue()));
            }
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
