package com.sideproject.withpt.application.auth.service;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.common.type.Role;

public interface OAuthLoginClient {
    Role role(); // 로그인 유저 타입 반환
    OAuthLoginResponse login(AuthLoginParams params);

}
