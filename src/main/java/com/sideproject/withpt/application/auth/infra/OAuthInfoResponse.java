package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.common.type.AuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    AuthProvider getOAuthProvider();
}
