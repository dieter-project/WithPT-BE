package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.common.type.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    OAuthProvider getOAuthProvider();
}
