package com.github.baoti.coding;

import android.app.Application;

import com.github.baoti.git.GitSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 15-3-19.
 */
@Module(
        library = true,
        complete = false
)
public class CodingModule {
    @Provides(type = Provides.Type.SET)
    GitSource gitSource(Application application) {
        return new CodingSource();
    }
}
