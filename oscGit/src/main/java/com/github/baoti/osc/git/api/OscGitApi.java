package com.github.baoti.osc.git.api;

import com.github.baoti.osc.git.OscGitProject;

import java.util.List;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface OscGitApi {
    String API_URL = "https://git.oschina.net/api/v3";

    @FormUrlEncoded
    @POST("/session")
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
    @GET("/projects/popular")
    Observable<List<OscGitProject>> listPopularProjects(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
