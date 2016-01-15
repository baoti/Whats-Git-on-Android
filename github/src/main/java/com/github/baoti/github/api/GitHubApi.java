package com.github.baoti.github.api;

import com.github.baoti.github.GitHubRepository;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface GitHubApi {
    String API_URL = "https://api.github.com/";

    @PUT("authorizations/clients/{client_id}")
    Observable<TokenResponse> authorize(@Path("client_id") String clientId, @Body TokenRequest request);

    /**
     * 分页获取所有公开项目列表
     */
    @GET("repositories")
    Observable<List<GitHubRepository>> listRepositories();
}
