package com.sideproject.withpt.application.auth.service;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.OAuthLoginParams;
import com.sideproject.withpt.application.type.Role;

public interface OAuthLoginClient {
    Role role(); // 로그인 유저 타입 반환
    OAuthLoginResponse login(OAuthLoginParams params);

}
