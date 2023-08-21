package com.sideproject.withpt.application.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL) // null 값 제외
public class OAuthLoginResponse {

    // 신규 회원
    private String email;
    private OAuthProvider oAuthProvider;
    private Role role;

    // 가입된 회원
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessExpiredAt;
    private Long refreshExpiredAt;

    public static OAuthLoginResponse of(OAuthInfoResponse oAuthInfoResponse, Role role) {
        return OAuthLoginResponse.builder()
            .email(oAuthInfoResponse.getEmail())
            .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
            .role(role)
            .build();
    }

    public static OAuthLoginResponse of(TokenSetDto tokenSetDto) {
        return OAuthLoginResponse.builder()
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }

}
