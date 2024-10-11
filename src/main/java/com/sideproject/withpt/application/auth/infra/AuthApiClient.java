package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.common.type.AuthProvider;

public interface AuthApiClient {
    AuthProvider authProvider(); // Client 의 타입 반환
    OAuthInfoResponse requestAuthInfo(String accessToken); // Access Token 을 기반으로 Email 이 포함된 프로필 정보를 획득
    default String requestAccessToken(AuthLoginParams params){  // Authorization Code 를 기반으로 인증 API 를 요청해서 Access Token 을 획득
        return "";
    }

}
