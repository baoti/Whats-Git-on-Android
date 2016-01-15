package com.github.baoti.coding.api;

import com.github.baoti.coding.CodingProject;
import com.github.baoti.coding.CodingUser;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface CodingApi {
    String API_URL = "https://coding.net/api/";

    @FormUrlEncoded
    @POST("login")
    Observable<Response<CodingResponse<CodingUser>>> login(
            @Field("email") String emailOrUserKey,
            @Field("password") String password,
            @Field("j_captcha") String captcha
    );

    /**
     * 分页获取公开项目列表
     */
    @GET("public/all")
    Observable<CodingResponse<Page<CodingProject>>> listProjects(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
