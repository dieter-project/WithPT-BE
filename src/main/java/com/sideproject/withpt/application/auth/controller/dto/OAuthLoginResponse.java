package com.sideproject.withpt.application.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.trainer.service.dto.TrainerSignUpResponse;
import com.sideproject.withpt.common.type.OAuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.domain.trainer.Trainer;
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

    private Long id;
    private String email;
    private String name;
    private OAuthProvider oAuthProvider;
    private Role role;

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

    public static OAuthLoginResponse of(Long id, String email, String name, OAuthProvider oAuthProvider, Role role, TokenSetDto tokenSetDto) {
        return OAuthLoginResponse.builder()
            .id(id)
            .email(email)
            .name(name)
            .oAuthProvider(oAuthProvider)
            .role(role)
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }

    public static OAuthLoginResponse of(Trainer trainer, TokenSetDto tokenSetDto) {
        return OAuthLoginResponse.builder()
            .id(trainer.getId())
            .email(trainer.getEmail())
            .name(trainer.getName())
            .oAuthProvider(trainer.getOauthProvider())
            .role(trainer.getRole())
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }

    public static OAuthLoginResponse of(TrainerSignUpResponse signUpResponse, TokenSetDto tokenSetDto) {
        return OAuthLoginResponse.builder()
            .id(signUpResponse.getId())
            .email(signUpResponse.getEmail())
            .name(signUpResponse.getName())
            .oAuthProvider(signUpResponse.getOAuthProvider())
            .role(signUpResponse.getRole())
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }
}
