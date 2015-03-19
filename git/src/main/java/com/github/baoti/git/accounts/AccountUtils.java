package com.github.baoti.git.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

import org.apache.http.MethodNotSupportedException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by liuyedong on 15-3-19.
 */
public class AccountUtils {
    private final AccountManager accountManager;

    @Inject
    public AccountUtils(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public Observable<Account> getAccount(final String accountType, final String authTokenType) {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return Observable.just(accounts[0]);
        }
        return Observable.create(new Observable.OnSubscribe<Account>() {
            @Override
            public void call(Subscriber<? super Account> subscriber) {
                Account[] accounts = accountManager.getAccountsByType(accountType);
                while (accounts.length == 0) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    try {
                        Bundle result = accountManager.addAccount(
                                accountType, authTokenType, null, null, null, null, null).getResult();
                        Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
                    } catch (Exception e) {
                        subscriber.onError(e);
                        return;
                    }
                }
            }
        });
    }

    public Observable<String> getAuthToken(Account account, String authTokenType, Bundle options) {
//        AccountManagerFuture<Bundle> future = manager.getAuthToken(account,
//                ACCOUNT_TYPE, false, null, null);
//
//        try {
//            Bundle result = future.getResult();
//            return result != null ? result.getString(KEY_AUTHTOKEN) : null;
//        } catch (AccountsException e) {
//            Log.e(TAG, "Auth token lookup failed", e);
//            return null;
//        } catch (IOException e) {
//            Log.e(TAG, "Auth token lookup failed", e);
//            return null;
//        }
//        return accountManager.getAuthToken(account, authTokenType, options, true, new AccountManagerCallback<Bundle>() {
//            @Override
//            public void run(AccountManagerFuture<Bundle> future) {
//
//            }
//        }, null);
        return Observable.error(new MethodNotSupportedException(""));
    }

    public void savePassword(String accountType, String name, String password) {
        Account account = new Account(name, accountType);
        if (!accountManager.addAccountExplicitly(account, password, null)) {
            accountManager.setPassword(account, password);
        }
    }
}
