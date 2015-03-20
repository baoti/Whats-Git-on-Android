package com.github.baoti.coding;

import com.github.baoti.coding.api.CodingResponse;

import java.net.HttpCookie;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.client.Header;
import retrofit.client.Response;

public class CodingSessionInterceptor implements RequestInterceptor {

    private static final String KEY_SESSION = "sid";

    private String sessionId;

    public void setSessionId(String s) {
        sessionId = s;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (sessionId != null) {
            request.addHeader("Cookie", "sid=" + sessionId);
        }
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
