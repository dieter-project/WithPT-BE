package com.sideproject.withpt.application.auth.infra.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.common.type.OAuthProvider;
import lombok.Getter;

public class KakaoInfoResponse implements OAuthInfoResponse {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {

        private String email;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }
}
