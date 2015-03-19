package com.github.baoti.whatsgit;

import android.accounts.AccountManager;
import android.app.Application;

import com.github.baoti.coding.CodingModule;
import com.github.baoti.git.GitSource;
import com.github.baoti.osc.git.OscGitModule;
import com.github.baoti.whatsgit.ui.UiModule;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(
        includes = {
                UiModule.class,
                CodingModule.class, OscGitModule.class
        }
)
public class AppMainModule {
    private final AppMain appMain;

    public AppMainModule(AppMain appMain) {
        this.appMain = appMain;
    }

    @Provides
    Application provideApplication() {
        return appMain;
    }

    @Singleton
    @Provides
    AccountManager provideAccountManager() {
        return AccountManager.get(appMain);
    }

    @Singleton
    @Provides
    GitSource[] provideGitSources(Set<GitSource> gitSources) {
        return gitSources.toArray(new GitSource[gitSources.size()]);
    }
}
