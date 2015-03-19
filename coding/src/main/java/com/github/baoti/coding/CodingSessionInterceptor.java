package com.github.baoti.coding;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.client.Header;
import retrofit.client.Response;

public class CodingSessionInterceptor implements RequestInterceptor {

    private String sessionId;

    public void setSessionId(String s) {
        sessionId = s;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (sessionId != null) {
            request.addHeader("Set-Cookie", "sessionId=" + sessionId);
        }
    }

    public static String fetchSessionId(Response response) {
        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            List<Header> headers = response.getHeaders();
            // TODO fetch from Cookie
        }
        return null;
    }
}
