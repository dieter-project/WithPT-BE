package com.sideproject.withpt.application.auth.controller;

import com.sideproject.withpt.application.auth.controller.dto.LogoutResponse;
import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.google.GoogleLoginParams;
import com.sideproject.withpt.application.auth.infra.kakao.KakaoLoginParams;
import com.sideproject.withpt.application.auth.service.OAuthLoginService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping ("/google")
    public ResponseEntity<OAuthLoginResponse> loginGoogle(@RequestBody GoogleLoginParams request) {
        return ResponseEntity.ok(oAuthLoginService.login(request));
    }

    @PostMapping("/kakao")
    public ResponseEntity<OAuthLoginResponse> loginKakao(@RequestBody KakaoLoginParams request) {
        return ResponseEntity.ok(oAuthLoginService.login(request));
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<LogoutResponse> logout(
        @AuthenticationPrincipal Long userId,
        @RequestHeader(name = "Authorization") String accessToken) {

        return ApiSuccessResponse.from(oAuthLoginService.logout(userId, accessToken));
    }
}
