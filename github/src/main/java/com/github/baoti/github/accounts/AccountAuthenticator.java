package com.github.baoti.github.accounts;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.github.baoti.git.Platform;
import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.BaseAccountAuthenticator;
import com.github.baoti.github.api.GitHubApi;
import com.github.baoti.github.api.TokenResponse;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.baoti.github.api.TokenRequest.authorize;

/**
 * Created by liuyedong on 15-1-19.
 */
public class AccountAuthenticator extends BaseAccountAuthenticator {
    @Inject
    OkHttpClient httpClient;
    private final GitHubApi api;
    private final PasswordInterceptor passwordInterceptor;

    public AccountAuthenticator(Context context) {
        super(context);
        Platform.inject(this);
        passwordInterceptor = new PasswordInterceptor();
        OkHttpClient client = httpClient.newBuilder()
                .addInterceptor(passwordInterceptor)
                .build();
        this.api = new Retrofit.Builder()
                .baseUrl(GitHubApi.API_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubApi.class);
    }

    @Override
    protected String getAuthToken(String accountName, String password) throws NetworkErrorException {
        passwordInterceptor.setPassword(accountName, password);
        try {
            TokenResponse session = authorize(api).toBlocking().first();
            return session.token;
        } catch (Throwable error) {
            throw new NetworkErrorException(error);
        }
    }

    @Override
    protected Intent onCreateAuthenticateIntent(Intent intent) {
        return AccountAuthenticatorActivity.putAuthenticateFragment(intent, LoginFragment.class);
    }
}
