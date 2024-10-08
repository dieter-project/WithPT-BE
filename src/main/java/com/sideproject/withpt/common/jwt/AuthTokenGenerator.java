package com.sideproject.withpt.common.jwt;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_VALID_TIME;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_VALID_TIME;

import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenSetDto generateTokenSet(Long memberId, Role role) {
        long now = (new Date()).getTime();

        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_VALID_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_VALID_TIME);

        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, role, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, role, refreshTokenExpiredAt);

        return TokenSetDto.of(
            accessToken,
            refreshToken,
            ACCESS_TOKEN_PREFIX,
            ACCESS_TOKEN_VALID_TIME / 1000L,
            REFRESH_TOKEN_VALID_TIME / 1000L);
    }

    public TokenSetDto generateAccessToken(Long memberId, Role role) {
        long now = (new Date()).getTime();

        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_VALID_TIME);

        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, role, accessTokenExpiredAt);

        return TokenSetDto.of(
            accessToken,
            ACCESS_TOKEN_PREFIX,
            ACCESS_TOKEN_VALID_TIME / 1000L);
    }

    public TokenSetDto generateRefreshToken(Long memberId, Role role) {
        long now = (new Date()).getTime();

        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_VALID_TIME);

        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, role, accessTokenExpiredAt);

        return TokenSetDto.of(
            accessToken,
            ACCESS_TOKEN_PREFIX,
            ACCESS_TOKEN_VALID_TIME / 1000L);
    }
}
