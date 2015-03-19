package com.github.baoti.whatsgit.ui;

import dagger.Module;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(
        complete = false,
        injects = {
                MainActivity.class,
                NavigationDrawerFragment.class
        }
)
public class UiModule {
}
