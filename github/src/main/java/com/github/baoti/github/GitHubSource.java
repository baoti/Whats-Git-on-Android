package com.github.baoti.github;

import android.app.Activity;

import com.github.baoti.git.GitSource;
import com.github.baoti.git.Repository;
import com.github.baoti.github.api.GitHubApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
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
    public GitHubSource(GitHubTokenInterceptor interceptor, OkHttpClient httpClient) {
        this.tokenInterceptor = interceptor;
        OkHttpClient client = httpClient.newBuilder()
                .addInterceptor(interceptor)
                .build();
        api = new Retrofit.Builder()
                .baseUrl(GitHubApi.API_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
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
