package com.github.baoti.github.accounts;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.BaseAccountAuthenticator;
import com.github.baoti.github.api.GitHubApi;
import com.github.baoti.github.api.TokenResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static com.github.baoti.github.api.TokenRequest.authorize;

/**
 * Created by liuyedong on 15-1-19.
 */
class AccountAuthenticator extends BaseAccountAuthenticator {
    private final GitHubApi api;
    private final PasswordInterceptor passwordInterceptor;

    public AccountAuthenticator(Context context) {
        super(context);
        passwordInterceptor = new PasswordInterceptor();
        this.api = new RestAdapter.Builder()
                .setEndpoint(GitHubApi.API_URL)
                .setRequestInterceptor(passwordInterceptor)
                .build()
                .create(GitHubApi.class);
    }

    @Override
    protected String getAuthToken(String accountName, String password) throws NetworkErrorException {
        passwordInterceptor.setPassword(accountName, password);
        try {
            TokenResponse session = authorize(api).toBlocking().first();
            return session.token;
        } catch (RetrofitError error) {
            throw new NetworkErrorException(error);
        }
    }

    @Override
    protected Intent onCreateAuthenticateIntent(Intent intent) {
        return AccountAuthenticatorActivity.putAuthenticateFragment(intent, LoginFragment.class);
    }
}
