package com.sideproject.withpt.common.jwt.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL) // null 값 제외
public class TokenSetDto {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessExpiredAt;
    private Long refreshExpiredAt;

    public static TokenSetDto of(String accessToken, String refreshToken, String grantType, Long accessExpiredAt,
        Long refreshExpiredAt) {
        return TokenSetDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .grantType(grantType)
            .accessExpiredAt(accessExpiredAt)
            .refreshExpiredAt(refreshExpiredAt)
            .build();

    }

    public static TokenSetDto of(String token, String grantType, Long expiredAt) {
        return TokenSetDto.builder()
            .accessToken(token)
            .grantType(grantType)
            .accessExpiredAt(expiredAt)
            .build();

    }
}
