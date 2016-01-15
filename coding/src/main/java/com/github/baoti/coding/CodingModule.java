package com.github.baoti.coding;

import android.app.Application;

import com.github.baoti.coding.accounts.AccountAuthenticator;
import com.github.baoti.coding.accounts.LoginFragment;
import com.github.baoti.git.GitSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                AccountAuthenticator.class,
                LoginFragment.class
        }
)
public class CodingModule {

    @Singleton
    @Provides
    @AccountType String accountType(Application app) {
        return app.getString(R.string.coding_account_type);
    }

    @Provides(type = Provides.Type.SET)
    GitSource gitSource(CodingSource source) {
        return source;
    }
}
