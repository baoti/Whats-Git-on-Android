package com.github.baoti.osc.git;

import android.app.Application;

import com.github.baoti.git.GitSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(library = true,
        complete = false)
public class OscGitModule {
    @Provides(type = Provides.Type.SET)
    GitSource gitSource(Application application) {
        return new OscGitSource();
    }
}
