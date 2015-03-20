package com.github.baoti.github.api;

import com.github.baoti.github.BuildConfig;
import com.github.baoti.github.GitHubConstants;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rx.Observable;

/**
 * Created by liuyedong on 15-3-20.
 */
public class TokenRequest {

    @SerializedName("client_secret")
    public String clientSecret;

    @SerializedName("scopes")
    public List<String> scopes;

    @SerializedName("note")
    public String note;

    @SerializedName("note_url")
    public String noteUrl;

    @SerializedName("fingerprint")
    public String fingerprint;

    public static TokenRequest createForUs() {
        TokenRequest request = new TokenRequest();
        request.clientSecret = BuildConfig.GITHUB_CLIENT_SECRET;
        request.scopes = GitHubConstants.SCOPES;
        request.note = GitHubConstants.APP_NOTE;
        request.noteUrl = GitHubConstants.APP_NOTE_URL;
        return request;
    }

    public static Observable<TokenResponse> authorize(GitHubApi api) {
        return api.authorize(BuildConfig.GITHUB_CLIENT_ID, createForUs());
    }
}
