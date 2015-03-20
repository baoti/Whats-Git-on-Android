package com.github.baoti.git.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

import com.github.baoti.git.util.Contracts;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by liuyedong on 15-3-19.
 */
public class AccountUtils {
    private final AccountManager accountManager;

    @Inject
    public AccountUtils(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public Observable<String> getAuthToken(final Account account, final String authTokenType) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //noinspection deprecation
                accountManager.getAuthToken(account, authTokenType, false,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }
                                String token;
                                try {
                                    token = future.getResult()
                                            .getString(AccountManager.KEY_AUTHTOKEN);
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                    return;
                                }
                                subscriber.onNext(token);
                                subscriber.onCompleted();
                            }
                        }, null);
            }
        });
    }

    public Observable<String> getAuthToken(Activity activity, String accountType, String authTokenType) {
        return getAuthTokenBundle(activity, accountType, authTokenType)
                .map(new Func1<Bundle, String>() {
                    @Override
                    public String call(Bundle bundle) {
                        return Contracts.notNull(
                                bundle.getString(AccountManager.KEY_AUTHTOKEN),
                                "authToken is null");
                    }
                });
    }

    public Observable<Bundle> getAuthTokenBundle(final Activity activity, final String accountType, final String authTokenType) {
        return Observable.create(new Observable.OnSubscribe<Bundle>() {
            @Override
            public void call(final Subscriber<? super Bundle> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (activity.isFinishing()) {
                    subscriber.onError(new OperationCanceledException("Activity had finished!"));
                    return;
                }
                accountManager.getAuthTokenByFeatures(accountType, authTokenType, null,
                        activity, null, null,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }
                                Bundle result;
                                try {
                                    result = future.getResult();
                                    String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                                    String type = result.getString(AccountManager.KEY_ACCOUNT_TYPE);
                                    String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                                    Timber.v("[getAuthTokenBundle] - name: %s, type: %s, token: %s", name, type, token);
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                    return;
                                }
                                subscriber.onNext(result);
                                subscriber.onCompleted();
                            }
                        }, null);
            }
        });
    }

    public void savePassword(Account account, String password) {
        if (!accountManager.addAccountExplicitly(account, password, null)) {
            accountManager.setPassword(account, password);
        }
    }

    public void saveAuthToken(Account account, String authTokenType, String authToken) {
        if (authToken != null) {
            accountManager.setAuthToken(account, authTokenType, authToken);
        }
    }
}
