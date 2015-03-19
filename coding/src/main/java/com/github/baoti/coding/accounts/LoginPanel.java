package com.github.baoti.coding.accounts;

import android.accounts.Account;
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

import com.github.baoti.coding.CodingConstants;
import com.github.baoti.coding.CodingSessionInterceptor;
import com.github.baoti.coding.R;
import com.github.baoti.coding.api.CodingApi;
import com.github.baoti.git.accounts.AccountUtils;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

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

    EditText captcha;

    Button login;

    private CodingApi api;
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
        accountType = res.getString(CodingConstants.ACCOUNT_TYPE_RES);
        api = new RestAdapter.Builder()
                .setEndpoint(CodingApi.API_URL)
                .build().create(CodingApi.class);
        accountUtils = new AccountUtils(AccountManager.get(context));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        captcha = (EditText) findViewById(R.id.captcha);
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
            email.setError(res.getString(R.string.coding_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError(res.getString(R.string.coding_invalid_password));
            return;
        }

        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        String captchaText = TextUtils.isEmpty(captcha.getText()) ? null : captcha.getText().toString();

        bindView(this, api.login(emailText, passwordText, captchaText)
                .map(new Func1<Response, String>() {
                    @Override
                    public String call(Response response) {
                        return CodingSessionInterceptor.fetchSessionId(response);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Account account = new Account(emailText, accountType);
                        accountUtils.savePassword(account, passwordText);
                        accountUtils.saveAuthToken(account,
                                CodingConstants.AUTH_TOKEN_TYPE, s);
                    }
                }))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Timber.v("login completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.v(e, "login failed");
                    }

                    @Override
                    public void onNext(String s) {
                        onLoginSuccess(emailText, passwordText, s);
                    }
                });
    }

    void onLoginSuccess(String email, String password, String s) {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(KEY_AUTHTOKEN, s);

        if (authenticatorActivity != null) {
            authenticatorActivity.setAccountAuthenticatorResult(intent.getExtras());
        }
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
