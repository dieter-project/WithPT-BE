package com.sideproject.withpt.application.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.trainer.service.dto.TrainerSignUpResponse;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.domain.trainer.Trainer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL) // null 값 제외
public class AuthLoginResponse {

    private Long id;
    private String email;
    private String name;
    private AuthProvider authProvider;
    private Role role;

    private String grantType;
    private String accessToken;
    private Long accessExpiredAt;
    private String refreshToken;
    private Long refreshExpiredAt;

    public static AuthLoginResponse of(OAuthInfoResponse oAuthInfoResponse, Role role) {
        return AuthLoginResponse.builder()
            .email(oAuthInfoResponse.getEmail())
            .authProvider(oAuthInfoResponse.getOAuthProvider())
            .role(role)
            .build();
    }

    public static AuthLoginResponse of(Long id, String email, String name, AuthProvider oAuthProvider, Role role, TokenSetDto tokenSetDto) {
        return AuthLoginResponse.builder()
            .id(id)
            .email(email)
            .name(name)
            .authProvider(oAuthProvider)
            .role(role)
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }

    public static AuthLoginResponse of(Trainer trainer, TokenSetDto tokenSetDto) {
        return AuthLoginResponse.builder()
            .id(trainer.getId())
            .email(trainer.getEmail())
            .name(trainer.getName())
            .authProvider(trainer.getAuthProvider())
            .role(trainer.getRole())
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }

    public static AuthLoginResponse of(TrainerSignUpResponse signUpResponse, TokenSetDto tokenSetDto) {
        return AuthLoginResponse.builder()
            .id(signUpResponse.getId())
            .email(signUpResponse.getEmail())
            .name(signUpResponse.getName())
            .authProvider(signUpResponse.getOAuthProvider())
            .role(signUpResponse.getRole())
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }
}
