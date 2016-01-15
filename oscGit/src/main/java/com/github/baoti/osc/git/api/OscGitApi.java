package com.github.baoti.osc.git.api;

import com.github.baoti.osc.git.OscGitProject;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface OscGitApi {
    String API_URL = "https://git.oschina.net/api/v3/";

    @FormUrlEncoded
    @POST("session")
    Observable<OscGitSession> login(
            @Field("email") String email,
            @Field("password") String password
    );

    /**
     * 分页获取热门项目列表
     * @param page
     * @param pageSize
     * @return
     */
    @GET("projects/popular")
    Observable<List<OscGitProject>> listPopularProjects(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
