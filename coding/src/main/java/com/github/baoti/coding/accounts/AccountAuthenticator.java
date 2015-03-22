package com.github.baoti.coding.accounts;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.github.baoti.coding.CodingSessionInterceptor;
import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.BaseAccountAuthenticator;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static com.github.baoti.coding.CodingUtils.passwordSha1;

/**
 * Created by liuyedong on 15-1-19.
 */
class AccountAuthenticator extends BaseAccountAuthenticator {
    private final CodingApi api;

    public AccountAuthenticator(Context context) {
        super(context);
        this.api = new RestAdapter.Builder()
                .setEndpoint(CodingApi.API_URL)
                .build()
                .create(CodingApi.class);
    }

    @Override
    protected String getAuthToken(String accountName, String password) throws NetworkErrorException {
        try {
            return CodingSessionInterceptor.fetchSessionId(
                    api.login(accountName, passwordSha1(password), null).toBlocking().first());
        } catch (RetrofitError error) {
            throw new NetworkErrorException(error);
        } catch (CodingSessionInterceptor.NoSessionException e) {
            return null;
        }
    }

    @Override
    protected Intent onCreateAuthenticateIntent(Intent intent) {
        return AccountAuthenticatorActivity.putAuthenticateFragment(intent, LoginFragment.class);
    }
}
