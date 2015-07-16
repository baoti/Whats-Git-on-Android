package com.github.baoti.github;

import android.app.Activity;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.github.api.GitHubApi;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.GsonConverterFactory;
import retrofit.ObservableCallAdapterFactory;
import retrofit.Retrofit;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by liuyedong on 15-3-19.
 */
@Singleton
public class GitHubSource implements GitSource {

    private final GitHubTokenInterceptor tokenInterceptor;
    private final GitHubApi api;

    @Inject
    public GitHubSource(GitHubTokenInterceptor interceptor) {
        this.tokenInterceptor = interceptor;
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(interceptor);
        api = new Retrofit.Builder()
                .baseUrl(GitHubApi.API_URL)
                .client(client)
                .callAdapterFactory(ObservableCallAdapterFactory.create())
                .converterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubApi.class);
    }

    @Override
    public String name() {
        return "GitHub";
    }

    @Override
    public Observable<List<? extends Repository>> getRepositories(Activity activity, PublishSubject<?> nextPageTrigger) {
        return tokenInterceptor.withToken(activity, api.listRepositories())
                .map(new Func1<List<GitHubRepository>, List<? extends Repository>>() {
                    @Override
                    public List<? extends Repository> call(List<GitHubRepository> gitHubRepositories) {
                        return gitHubRepositories;
                    }
                });
    }

    @Override
    public String toString() {
        return name();
    }
}
