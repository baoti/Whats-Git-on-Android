package com.github.baoti.osc.git;

import android.app.Application;

import com.github.baoti.git.GitSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(library = true,
        complete = false
)
public class OscGitModule {

    @Singleton
    @Provides
    @AccountType String accountType(Application app) {
        return app.getString(OscGitConstants.ACCOUNT_TYPE_RES);
    }

    @Provides(type = Provides.Type.SET)
    GitSource gitSource(OscGitSource source) {
        return source;
    }
}
