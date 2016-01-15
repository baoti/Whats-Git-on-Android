package com.github.baoti.github.accounts;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.github.baoti.git.util.Texts.basicAuthorization;

class PasswordInterceptor implements Interceptor {
    private String username;
    private String password;

    public void setPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("User-Agent", "Whats-Git-on-Android")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", basicAuthorization(username, password))
                .build();
        return chain.proceed(request);
    }
}
