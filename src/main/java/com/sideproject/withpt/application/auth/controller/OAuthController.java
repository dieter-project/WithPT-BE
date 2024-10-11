package com.sideproject.withpt.application.auth.controller;

import com.sideproject.withpt.application.auth.service.dto.LoginResponse;
import com.sideproject.withpt.application.auth.service.dto.LogoutResponse;
import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.auth.controller.dto.ReissueReqeust;
import com.sideproject.withpt.application.auth.service.dto.ReissueResponse;
import com.sideproject.withpt.application.auth.infra.google.GoogleLoginParams;
import com.sideproject.withpt.application.auth.infra.kakao.KakaoLoginParams;
import com.sideproject.withpt.application.auth.infra.password.PasswordLoginParams;
import com.sideproject.withpt.application.auth.service.OAuthLoginService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/api/v1/auth/login")
    public ApiSuccessResponse<LoginResponse> login(@RequestBody PasswordLoginParams request) {
        return ApiSuccessResponse.from(oAuthLoginService.login(request));
    }

    @PostMapping("/api/v1/oauth/google")
    public ApiSuccessResponse<LoginResponse> loginGoogle(@RequestBody GoogleLoginParams request) {
        return ApiSuccessResponse.from(oAuthLoginService.login(request));
    }

    @PostMapping("/api/v1/oauth/kakao")
    public ApiSuccessResponse<LoginResponse> loginKakao(@RequestBody KakaoLoginParams request) {
        return ApiSuccessResponse.from(oAuthLoginService.login(request));
    }

    @PostMapping("/api/v1/oauth/logout")
    public ApiSuccessResponse<LogoutResponse> logout(
        @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
        @RequestHeader(name = "Authorization") String accessToken) {

        return ApiSuccessResponse.from(oAuthLoginService.logout(userId, accessToken));
    }

    @PostMapping("/api/v1/oauth/reissue")
    public ApiSuccessResponse<ReissueResponse> reissue(
        @RequestHeader(name = "Authorization") String accessToken,
        @RequestBody ReissueReqeust request
    ) {
        return ApiSuccessResponse.from(oAuthLoginService.reissue(accessToken, request.getRefresh()));
    }
}
