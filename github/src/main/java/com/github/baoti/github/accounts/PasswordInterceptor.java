package com.github.baoti.github.accounts;

import retrofit.RequestInterceptor;

import static com.github.baoti.git.util.Texts.basicAuthorization;

class PasswordInterceptor implements RequestInterceptor {
    private String username;
    private String password;

    public void setPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "Whats-Git-on-Android");
        request.addHeader("Accept", "application/vnd.github.v3+json");

        request.addHeader("Authorization", basicAuthorization(username, password));
    }
}
