package com.sideproject.withpt.application.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class ReissueResponse {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessExpiredAt;
    private Long refreshExpiredAt;

    public static ReissueResponse of(TokenSetDto tokenSetDto) {
        return ReissueResponse.builder()
            .grantType(tokenSetDto.getGrantType())
            .accessToken(tokenSetDto.getAccessToken())
            .accessExpiredAt(tokenSetDto.getAccessExpiredAt())
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshExpiredAt(tokenSetDto.getRefreshExpiredAt())
            .build();
    }
}
