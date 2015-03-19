package com.github.baoti.git.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liuyedong on 15-3-19.
 */
public class LoginActivity extends AccountAuthenticatorActivity {
    private static final String KEY_ACCOUNT_TYPE = "app:accountType";
    private static final String KEY_AUTH_TOKEN_TYPE = "app:authTokenType";
    private static final String KEY_LAYOUT_RES = "app:layoutRes";

    public static Intent actionAuthenticate(Context context,
                                            AccountAuthenticatorResponse response,
                                            String accountType,
                                            String authTokenType,
                                            int layoutRes) {
        return new Intent(context, LoginActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra(KEY_ACCOUNT_TYPE, accountType)
                .putExtra(KEY_AUTH_TOKEN_TYPE, authTokenType)
                .putExtra(KEY_LAYOUT_RES, layoutRes);
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        int layoutRes = getIntent().getIntExtra(KEY_LAYOUT_RES, 0);
        if (layoutRes == 0) {
            finish();
        }
        setContentView(layoutRes);
    }
}
