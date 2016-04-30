package com.github.baoti.git;

import android.accounts.AccountManager;
import android.app.Application;
import android.provider.Settings;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import timber.log.Timber;

public class Platform {

    private static Platform platform;

    public static void initialize(Component component) {
        if (platform != null) {
            throw new IllegalStateException("has initialized");
        }
        platform = new Platform(component);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private static Platform instance() {
        if (platform == null) {
            throw new IllegalStateException("platform not initialized");
        }
        return platform;
    }

    private final Component component;

    private Platform(Component component) {
        this.component = component;
    }

    public static Component component() {
        return instance().component;
    }

    public static Application app() {
        return component().app();
    }

    public static String getAndroidUserId() {
        return Settings.System.getString(
                app().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @dagger.Module
    public static class Module {
        private final Application application;

        public Module(Application application) {
            this.application = application;
        }

        @Provides
        Application providesApplication() {
            return application;
        }

        @Singleton
        @Provides
        AccountManager providesAccountManager() {
            return AccountManager.get(application);
        }

        @Provides
        @ElementsIntoSet
        Set<GitSource> providesGitSourceSet() {
            return new HashSet<>();
        }

        @Singleton
        @Provides
        GitSource[] providesGitSources(Set<GitSource> gitSources) {
            return gitSources.toArray(new GitSource[gitSources.size()]);
        }
    }

//    @Singleton
//    @dagger.Component(modules = Module.class)
    public interface Component {

        Application app();

        AccountManager accountManager();

        GitSource[] gitSources();
    }
}
