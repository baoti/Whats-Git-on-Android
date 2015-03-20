package com.github.baoti.github;

import android.app.Activity;
import android.util.Base64;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.accounts.AuthTokenProvider;
import com.github.baoti.git.util.Texts;

import retrofit.RequestInterceptor;
import rx.Observable;

public class GitHubTokenInterceptor extends AuthTokenProvider implements RequestInterceptor {
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

        String token = getAuthToken();
        if (token != null) {
            request.addHeader("Authorization", "Basic " + encodeBasic(token, ""));
        } else if (username != null && password != null) {
            request.addHeader("Authorization", "Basic " + encodeBasic(username, password));
        }
    }

    public Observable<String> withToken(Activity activity, AccountUtils accountUtils) {
        return provideAuthToken(activity, accountUtils,
                activity.getString(GitHubConstants.ACCOUNT_TYPE_RES),
                GitHubConstants.AUTH_TOKEN_TYPE);
    }

    public static String encodeBasic(String username, String password) {
        String text = username + ":" + Texts.str(password);
        return Base64.encodeToString(text.getBytes(Texts.UTF_8), Base64.DEFAULT);
    }
}
