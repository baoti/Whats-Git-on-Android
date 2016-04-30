package com.github.baoti.osc.git;

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
public abstract class OscGitModule {

    @Singleton
    @Provides
    @AccountType static String accountType(Application app) {
        return app.getString(OscGitConstants.ACCOUNT_TYPE_RES);
    }

    @Provides
    @IntoSet
    static GitSource gitSource(OscGitSource source) {
        return source;
    }
}
