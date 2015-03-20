package com.github.baoti.coding.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * Created by liuyedong on 15-3-19.
 */
public class CodingResponse<D> {
    public int code;
    public D data;
    public Map<String, String> msg;

    public static boolean isSuccessful(Response response) {
        if (response.getStatus() != 200) {
            return false;
        }
        TypedInput body = response.getBody();
        if (body == null) {
            return false;
        }
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(body.in(), "UTF-8");
        } catch (IOException e) {
            return false;
        }
        CodingResponse obj;
        try {
            obj = new Gson().fromJson(isr, CodingResponse.class);
        } catch (Exception e) {
            return false;
        }
        return obj.code == 0;
    }
}
