package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.application.type.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    OAuthProvider getOAuthProvider();
}
