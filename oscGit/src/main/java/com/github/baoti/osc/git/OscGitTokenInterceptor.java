package com.github.baoti.osc.git;

import retrofit.RequestInterceptor;

public class OscGitTokenInterceptor implements RequestInterceptor {
    private String token;

    public void setToken(String s) {
        token = s;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (token != null) {
            request.addQueryParam(OscGitConstants.QUERY_PARAM_TOKEN, token);
        }
    }
}
