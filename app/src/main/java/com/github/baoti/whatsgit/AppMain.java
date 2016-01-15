package com.github.baoti.whatsgit;

import android.app.Application;
import android.content.Context;

import com.github.baoti.coding.CodingModule;
import com.github.baoti.git.Platform;
import com.github.baoti.github.GitHubModule;
import com.github.baoti.osc.git.OscGitModule;
import com.github.baoti.whatsgit.ui.UiModule;

/**
 * Created by liuyedong on 15-3-19.
 */
public class AppMain extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Platform.initialize(this,
                UiModule.class,
                CodingModule.class,
                OscGitModule.class,
                GitHubModule.class);
    }
}
