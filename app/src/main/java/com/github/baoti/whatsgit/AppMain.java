package com.github.baoti.whatsgit;

import android.app.Application;
import android.provider.Settings;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by liuyedong on 15-3-19.
 */
public class AppMain extends Application {

    private static AppMain app;

    public static AppMain app() {
        return app;
    }

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        app = this;

        super.onCreate();

        graph = ObjectGraph.create(new AppMainModule(this));

        globalGraph().injectStatics();

        if (BuildConfig.DEBUG) {
            globalGraph().validate(); // validate dagger's object graph
            Timber.plant(new Timber.DebugTree());
        }
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    public ObjectGraph getScopedGraph() {
        return graph;
    }

    public static ObjectGraph globalGraph() {
        if (app == null) {
            throw new IllegalStateException("app is null");
        }
        return app.getScopedGraph();
    }

    public String getAndroidUserId() {
        return Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
