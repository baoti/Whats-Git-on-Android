package com.github.baoti.github.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.baoti.git.accounts.AccountAuthenticatorActivity;
import com.github.baoti.git.accounts.AccountUtils;
import com.github.baoti.git.util.Contracts;
import com.github.baoti.github.GitHubConstants;
import com.github.baoti.github.R;
import com.github.baoti.github.api.GitHubApi;
import com.github.baoti.github.api.TokenResponse;

import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static butterknife.ButterKnife.findById;
import static com.github.baoti.github.api.TokenRequest.authorize;
import static rx.android.app.AppObservable.bindFragment;

/**
 * Created by liuyedong on 15-3-19.
 */
public class LoginFragment extends Fragment {

    EditText email;

    EditText password;

    Button login;

    private PasswordInterceptor passwordInterceptor;
    private GitHubApi api;
    private String accountType;
    private AccountUtils accountUtils;

    private Subscription loginSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountType = getString(GitHubConstants.ACCOUNT_TYPE_RES);
        passwordInterceptor = new PasswordInterceptor();
        api = new RestAdapter.Builder()
                .setEndpoint(GitHubApi.API_URL)
                .setRequestInterceptor(passwordInterceptor)
                .build()
                .create(GitHubApi.class);
        accountUtils = new AccountUtils(AccountManager.get(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.github_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        email = findById(view, R.id.email);
        password = findById(view, R.id.password);
        login = findById(view, R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
            loginSubscription = null;
        }
        super.onDestroy();
    }

    private void onLoginClick() {
        if (TextUtils.isEmpty(email.getText())) {
            email.setError(getString(R.string.github_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError(getString(R.string.github_invalid_password));
            return;
        }

        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();

        passwordInterceptor.setPassword(emailText, passwordText);
        loginSubscription = bindFragment(this, authorize(api)
                .map(new Func1<TokenResponse, String>() {
                    @Override
                    public String call(TokenResponse tokenResponse) {
                        return Contracts.notNull(tokenResponse.token, "token is null");
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Account account = new Account(emailText, accountType);
                        accountUtils.savePassword(account, passwordText);
                        accountUtils.saveAuthToken(account,
                                GitHubConstants.AUTH_TOKEN_TYPE, s);
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
                        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String s) {
                        onLoginSuccess(emailText, s);
                    }
                });
    }

    void onLoginSuccess(String email, String token) {
        // SHOULD BE STILL ATTACHED
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(KEY_AUTHTOKEN, token);

        if (activity instanceof AccountAuthenticatorActivity) {
            ((AccountAuthenticatorActivity) activity).setAccountAuthenticatorResult(intent.getExtras());
        }
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
