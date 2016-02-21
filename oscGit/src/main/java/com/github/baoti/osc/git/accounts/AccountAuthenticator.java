package com.github.baoti.osc.git.accounts;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.github.baoti.git.Platform;
import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.BaseAccountAuthenticator;
import com.github.baoti.osc.git.api.OscGitApi;
import com.github.baoti.osc.git.api.OscGitSession;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuyedong on 15-1-19.
 */
public class AccountAuthenticator extends BaseAccountAuthenticator {
    @Inject
    OkHttpClient httpClient;
    private final OscGitApi api;

    public AccountAuthenticator(Context context) {
        super(context);
        Platform.inject(this);
        this.api = new Retrofit.Builder()
                .baseUrl(OscGitApi.API_URL)
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OscGitApi.class);
    }

    @Override
    protected String getAuthToken(String accountName, String password) throws NetworkErrorException {
        OscGitSession session;
        try {
            session = api.login(accountName, password).toBlocking().first();
        } catch (Throwable error) {
            throw new NetworkErrorException(error);
        }
        return session.private_token;
    }

    @Override
    protected Intent onCreateAuthenticateIntent(Intent intent) {
        return AccountAuthenticatorActivity.putAuthenticateFragment(intent, LoginFragment.class);
    }
}
