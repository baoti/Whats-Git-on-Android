package com.github.baoti.git;

import android.accounts.AccountManager;
import android.app.Application;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dagger.ObjectGraph;
import dagger.Provides;
import okhttp3.OkHttpClient;
import timber.log.Timber;

@dagger.Module(library = true)
public class Platform {

    private static Platform platform;

    public static void initialize(Application app, Object... modules) {
        if (platform != null) {
            throw new IllegalStateException("has initialized");
        }
        platform = new Platform(app, modules);

        if (BuildConfig.DEBUG) {
            platform.graph.validate(); // validate dagger's object graph
            Timber.plant(new Timber.DebugTree());
        }
    }

    private static Platform instance() {
        if (platform == null) {
            throw new IllegalStateException("platform not initialized");
        }
        return platform;
    }

    private final Application application;

    private final ObjectGraph graph;

    private Platform(Application app, Object... modules) {
        this.application = app;
        ArrayList<Object> allModules = new ArrayList<>();
        allModules.add(this);
        Collections.addAll(allModules, modules);
        this.graph = ObjectGraph.create(allModules.toArray());
    }

    public static Application app() {
        return instance().application;
    }

    public static void inject(Object object) {
        instance().graph.inject(object);
    }

    public static ObjectGraph globalGraph() {
        return instance().graph;
    }

    public static String getAndroidUserId() {
        return Settings.System.getString(
                instance().application.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Provides
    Application providesApplication() {
        return application;
    }

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Singleton
    @Provides
    AccountManager providesAccountManager() {
        return AccountManager.get(application);
    }

    @Provides(type = Provides.Type.SET_VALUES)
    Set<GitSource> providesGitSources() {
        return new HashSet<>();
    }

    @Singleton
    @Provides
    GitSource[] providesGitSources(Set<GitSource> gitSources) {
        return gitSources.toArray(new GitSource[gitSources.size()]);
    }
}
