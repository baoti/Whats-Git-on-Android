package com.github.baoti.github;

import android.util.Base64;

import com.github.baoti.git.util.Texts;

import retrofit.RequestInterceptor;

public class GitHubTokenInterceptor implements RequestInterceptor {
    private String username;
    private String password;
    private String token;

    public void setPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setToken(String s) {
        token = s;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "Whats-Git-on-Android");
        request.addHeader("Accept", "application/vnd.github.v3+json");
        if (token != null) {
            request.addHeader("Authorization", "Basic " + encodeBasic(token, ""));
        } else if (username != null && password != null) {
            request.addHeader("Authorization", "Basic " + encodeBasic(username, password));
        }
    }

    public static String encodeBasic(String username, String password) {
        String text = username + ":" + Texts.str(password);
        return Base64.encodeToString(text.getBytes(Texts.UTF_8), Base64.DEFAULT);
    }
}
