package com.github.baoti.github;

import android.app.Application;

import com.github.baoti.git.GitSource;
import com.github.baoti.github.accounts.AccountAuthenticator;
import com.github.baoti.github.accounts.LoginFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(library = true,
        complete = false,
        injects = {
                AccountAuthenticator.class,
                LoginFragment.class
        }
)
public class GitHubModule {

    @Singleton
    @Provides
    @AccountType String accountType(Application app) {
        return app.getString(GitHubConstants.ACCOUNT_TYPE_RES);
    }

    @Provides(type = Provides.Type.SET)
    GitSource gitSource(GitHubSource source) {
        return source;
    }
}
