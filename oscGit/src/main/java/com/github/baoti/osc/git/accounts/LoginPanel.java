package com.github.baoti.osc.git.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.osc.git.OscGitConstants;
import com.github.baoti.osc.git.R;
import com.github.baoti.osc.git.api.OscGitApi;
import com.github.baoti.osc.git.api.OscGitSession;

import retrofit.RestAdapter;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static rx.android.view.ViewObservable.bindView;

/**
 * Created by liuyedong on 15-3-19.
 */
public class LoginPanel extends LinearLayout {

    private Resources res;

    private Activity activity;
    private AccountAuthenticatorActivity authenticatorActivity;

//    @FindView(R.id.email)
    EditText email;

//    @FindView(R.id.password)
    EditText password;

    Button login;

    private Subscription subscription = null;

    private OscGitApi api;
    private String accountType;
    private AccountUtils accountUtils;

    public LoginPanel(Context context) {
        super(context);
        init(context);
    }

    public LoginPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        activity = (Activity) context;
        if (context instanceof AccountAuthenticatorActivity) {
            authenticatorActivity = (AccountAuthenticatorActivity) context;
        }
        res = getResources();
        accountType = res.getString(OscGitConstants.ACCOUNT_TYPE_RES);
        api = new RestAdapter.Builder()
                .setEndpoint(OscGitApi.API_URL)
                .build().create(OscGitApi.class);
        accountUtils = new AccountUtils(AccountManager.get(context));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
    }

    private void onLoginClick() {
        if (TextUtils.isEmpty(email.getText())) {
            email.setError(res.getString(R.string.osc_git_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError(res.getString(R.string.osc_git_invalid_password));
            return;
        }

        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();

        bindView(this, api.login(emailText, passwordText)
                .map(new Func1<OscGitSession, String>() {
                    @Override
                    public String call(OscGitSession oscGitSession) {
                        return oscGitSession.private_token;
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        accountUtils.savePassword(accountType, emailText, passwordText);
                    }
                }))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        onLoginSuccess(emailText, passwordText, s);
                    }
                });
    }

    void onLoginSuccess(String email, String password, String token) {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(KEY_AUTHTOKEN, token);

        if (authenticatorActivity != null) {
            authenticatorActivity.setAccountAuthenticatorResult(intent.getExtras());
        }
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
