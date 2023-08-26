package com.sideproject.withpt.application.auth.infra.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.type.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
public class GoogleInfoResponse implements OAuthInfoResponse {

    @JsonProperty("google-account")
    private GoogleAccount googleAccount;

    @Getter
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GoogleAccount {

        private String email;
    }

    @Override
    public String getEmail() {
        return googleAccount.email;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.GOOGLE;
    }
}
