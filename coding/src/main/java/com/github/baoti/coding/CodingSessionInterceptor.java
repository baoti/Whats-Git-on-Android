package com.github.baoti.coding;

import android.app.Activity;

import com.github.baoti.coding.api.CodingResponse;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;

import java.net.HttpCookie;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Observable;

public class CodingSessionInterceptor extends AuthTokenProvider implements RequestInterceptor {

    private static final String KEY_SESSION = "sid";

    @Override
    public void intercept(RequestFacade request) {
        String sessionId = getAuthToken();
        if (sessionId != null) {
            request.addHeader("Cookie", KEY_SESSION + "=" + sessionId);
        }
    }

    public Observable<String> withSession(Activity activity, AccountUtils accountUtils) {
        return provideAuthToken(activity, accountUtils,
                activity.getString(CodingConstants.ACCOUNT_TYPE_RES),
                CodingConstants.AUTH_TOKEN_TYPE);
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

        public NoSessionException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public NoSessionException(Throwable throwable) {
            super(throwable);
        }
    }
}
