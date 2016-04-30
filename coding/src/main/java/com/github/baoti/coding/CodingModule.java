package com.github.baoti.coding;

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
public abstract class CodingModule {

    @Singleton
    @Provides
    @AccountType static String accountType(Application app) {
        return app.getString(R.string.coding_account_type);
    }

    @Provides
    @IntoSet
    static GitSource gitSource(CodingSource source) {
        return source;
    }
}
