package com.github.baoti.github;

import android.app.Application;

import com.github.baoti.git.GitSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module
public abstract class GitHubModule {

    @Singleton
    @Provides
    @AccountType static String accountType(Application app) {
        return app.getString(GitHubConstants.ACCOUNT_TYPE_RES);
    }

    @Provides
    @IntoSet
    static GitSource gitSource(GitHubSource source) {
        return source;
    }
}
