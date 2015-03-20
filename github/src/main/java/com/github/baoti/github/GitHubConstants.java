package com.github.baoti.github;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liuyedong on 15-3-19.
 */
public interface GitHubConstants {

    int ACCOUNT_TYPE_RES = R.string.github_account_type;

    String AUTH_TOKEN_TYPE = "hashed_token";

    /**
     * Application note URL
     */
    String APP_NOTE_URL = "https://github.com/baoti/Whats-Git-on-Android";

    /**
     * Application note
     */
    String APP_NOTE = "Whats Git Android App";

    List<String> SCOPES = Arrays.asList("repo");
}
