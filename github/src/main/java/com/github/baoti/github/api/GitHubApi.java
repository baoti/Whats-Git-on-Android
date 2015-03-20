package com.github.baoti.github.api;

import com.github.baoti.github.GitHubRepository;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface GitHubApi {
    String API_URL = "https://api.github.com";

    @PUT("/authorizations/clients/{client_id}")
    Observable<TokenResponse> authorize(@Path("client_id") String clientId, @Body TokenRequest request);

    /**
     * 分页获取所有公开项目列表
     */
    @GET("/repositories")
    Observable<List<GitHubRepository>> listRepositories();
}
