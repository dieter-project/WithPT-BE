package com.sideproject.withpt.application.auth.controller;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.kakao.KakaoLoginParams;
import com.sideproject.withpt.application.auth.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/Google")
    public void loginGoogle() {

    }

    @PostMapping("/Kakao")
    public ResponseEntity<OAuthLoginResponse> loginKakao(@RequestBody KakaoLoginParams request) {
        return ResponseEntity.ok(oAuthLoginService.login(request));
    }
}
