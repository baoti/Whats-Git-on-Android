package com.github.baoti.git.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import timber.log.Timber;

import static com.github.baoti.git.accounts.AccountAuthenticatorActivity.putAccountName;

public abstract class BaseAccountAuthenticator extends AbstractAccountAuthenticator {

    protected final Context context;
    protected final AccountManager accountManager;

    public BaseAccountAuthenticator(Context context) {
        super(context);
        this.context = context;
        accountManager = AccountManager.get(context);
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
        result.putParcelable(AccountManager.KEY_INTENT,
                onCreateAuthenticateIntent(createAuthenticateIntent(response, accountType, authTokenType)));
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

        Intent authenticateIntent = putAccountName(createAuthenticateIntent(
                response, account.type, authTokenType), account.name);
        Bundle result = new Bundle();

        String password = accountManager.getPassword(account);
        if (TextUtils.isEmpty(password)) {
            result.putParcelable(AccountManager.KEY_INTENT, onCreateAuthenticateIntent(authenticateIntent));
            return result;
        }

        String authToken;
        try {
            authToken = getAuthToken(account.name, password);
        } catch (NetworkErrorException e) {
            throw e;
        } catch (Throwable throwable) {
            Timber.w(throwable, "Fail to get auth token");
            authToken = null;
        }

        if (TextUtils.isEmpty(authToken)) {
            result.putParcelable(AccountManager.KEY_INTENT, onCreateAuthenticateIntent(authenticateIntent));
        } else {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
//            accountManager.clearPassword(account);
        }
        return result;
    }

    protected abstract String getAuthToken(String accountName, String password) throws NetworkErrorException;

    /**
     * 子类通常需要调用 putAuthenticateFragment
     */
    protected abstract Intent onCreateAuthenticateIntent(Intent intent);

    protected Intent createAuthenticateIntent(
            AccountAuthenticatorResponse response, String accountType, String authTokenType) {
        return AccountAuthenticatorActivity.buildIntent(context, response, accountType, authTokenType);
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Timber.d("[updateCredentials] - account: %s, authTokenType: %s", account, authTokenType);
        // authToken 失效 或 密码变更时, 此方法可重新请求 authToken 或 要求用户登录
        Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, onCreateAuthenticateIntent(
                putAccountName(createAuthenticateIntent(response, account.type, authTokenType),
                        account.name)));
        return result;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }
}
