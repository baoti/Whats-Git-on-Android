package com.github.baoti.whatsgit;

import android.app.Application;

import com.github.baoti.git.Platform;

/**
 * Created by liuyedong on 15-3-19.
 */
public class AppMain extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Platform.Module platformModule = new Platform.Module(this);
        AppComponent component = DaggerAppComponent.builder()
                .module(platformModule)
                .build();
        Platform.initialize(component);
    }
}
