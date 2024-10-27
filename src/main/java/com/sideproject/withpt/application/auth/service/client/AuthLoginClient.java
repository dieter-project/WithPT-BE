package com.sideproject.withpt.application.auth.service.client;

import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.application.auth.service.response.LoginResponse;
import com.sideproject.withpt.common.type.Role;

public interface AuthLoginClient {
    Role role(); // 로그인 유저 타입 반환
    LoginResponse login(AuthLoginParams params);

}
