package com.github.baoti.whatsgit;

import com.github.baoti.coding.CodingModule;
import com.github.baoti.git.Platform;
import com.github.baoti.github.GitHubModule;
import com.github.baoti.osc.git.OscGitModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sean on 2015/5/23.
 */
@Singleton
@Component(
        modules = {
                Platform.Module.class,
                CodingModule.class,
                OscGitModule.class,
                GitHubModule.class
        }
)
public interface AppComponent extends Platform.Component {

}
