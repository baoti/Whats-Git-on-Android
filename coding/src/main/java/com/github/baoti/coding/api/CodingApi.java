package com.github.baoti.coding.api;

import com.github.baoti.coding.CodingProject;
import com.github.baoti.coding.CodingUser;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface CodingApi {
    String API_URL = "https://coding.net/api";

    @FormUrlEncoded
    @POST("/login")
    CodingResponse<CodingUser> login(
            @Field("email") String emailOrUserKey,
            @Field("password") String password,
            @Field("j_captcha") String captcha
    );

    /**
     * 分页获取公开项目列表
     */
    @GET("/public/all")
    CodingResponse<Page<CodingProject>> listProjects(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
