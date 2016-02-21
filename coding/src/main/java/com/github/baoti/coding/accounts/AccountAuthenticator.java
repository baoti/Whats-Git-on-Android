package com.github.baoti.coding.accounts;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.github.baoti.coding.CodingSessionInterceptor;
import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.git.Platform;
import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.BaseAccountAuthenticator;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.baoti.coding.CodingUtils.passwordSha1;

/**
 * Created by liuyedong on 15-1-19.
 */
public class AccountAuthenticator extends BaseAccountAuthenticator {
    @Inject
    OkHttpClient httpClient;
    private final CodingApi api;

    public AccountAuthenticator(Context context) {
        super(context);
        Platform.inject(this);
        this.api = new Retrofit.Builder()
                .baseUrl(CodingApi.API_URL)
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CodingApi.class);
    }

    @Override
    protected String getAuthToken(String accountName, String password) throws NetworkErrorException {
        try {
            return CodingSessionInterceptor.fetchSessionId(
                    api.login(accountName, passwordSha1(password), null).toBlocking().first());
        } catch (CodingSessionInterceptor.NoSessionException e) {
            return null;
        } catch (RuntimeException error) {
            throw new NetworkErrorException(error);
        }
    }

    @Override
    protected Intent onCreateAuthenticateIntent(Intent intent) {
        return AccountAuthenticatorActivity.putAuthenticateFragment(intent, LoginFragment.class);
    }
}
