package com.github.baoti.coding.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.baoti.coding.CodingConstants;
import com.github.baoti.coding.CodingSessionInterceptor;
import com.github.baoti.coding.R;
import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.git.accounts.LoginActivity;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by liuyedong on 15-1-19.
 */
class AccountAuthenticator extends AbstractAccountAuthenticator {
    private final Context context;
    private final CodingApi api;
    private final String accountType;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
        accountType = context.getString(CodingConstants.ACCOUNT_TYPE_RES);
        this.api = new RestAdapter.Builder()
                .setEndpoint(CodingApi.API_URL)
                .build().create(CodingApi.class);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        // 用于显示设置界面
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Timber.d("[addAccount] - accountType: %s, authTokenType: %s", accountType, authTokenType);
        Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, createActivityIntent(response, authTokenType));
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        // 用于明确提醒用户进行登录, options中可携带KEY_PASSWORD, 用于直接登录
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Timber.d("[getAuthToken] - account: %s, authTokenType: %s", account, authTokenType);

        Bundle result = new Bundle();

        AccountManager am = AccountManager.get(context);
        String password = am.getPassword(account);
        if (TextUtils.isEmpty(password)) {
            result.putParcelable(AccountManager.KEY_INTENT, createActivityIntent(response, authTokenType));
            return result;
        }

        String authToken;
        try {
            authToken = CodingSessionInterceptor.fetchSessionId(
                    api.login(account.name, password, null).toBlocking().first());
        } catch (RetrofitError error) {
            throw new NetworkErrorException(error);
        } catch (Throwable throwable) {
            Timber.w(throwable, "[getAuthToken]");
            return result;
        }

        if (TextUtils.isEmpty(authToken)) {
            result.putParcelable(AccountManager.KEY_INTENT, createActivityIntent(response, authTokenType));
        } else {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            am.clearPassword(account);
        }
        return result;
    }

    private Intent createActivityIntent(AccountAuthenticatorResponse response, String authTokenType) {
        return LoginActivity.actionAuthenticate(context, response, accountType, authTokenType,
                R.layout.panel_login);
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // authToken 失效 或 密码变更时, 此方法可重新请求 authToken 或 要求用户登录
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }
}
