package com.sideproject.withpt.application.auth.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_VALID_TIME;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.service.dto.LoginResponse;
import com.sideproject.withpt.application.auth.service.dto.LogoutResponse;
import com.sideproject.withpt.application.auth.service.dto.AuthLoginResponse;
import com.sideproject.withpt.application.auth.service.dto.ReissueResponse;
import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.JwtTokenProvider;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OAuthLoginService {

    private final Map<Role, OAuthLoginClient> loginClients;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthTokenGenerator authTokenGenerator;
    private final RedisClient redisClient;

    private static final String LOGOUT_MESSAGE = "로그아웃되셨습니다.";

    public OAuthLoginService(List<OAuthLoginClient> clients,
        JwtTokenProvider jwtTokenProvider,
        AuthTokenGenerator authTokenGenerator,
        RedisClient redisClient) {
        this.loginClients = clients.stream().collect(
            Collectors.toUnmodifiableMap(OAuthLoginClient::role, Function.identity())
        );

        this.jwtTokenProvider = jwtTokenProvider;
        this.authTokenGenerator = authTokenGenerator;
        this.redisClient = redisClient;
    }

    @Transactional
    public LoginResponse login(AuthLoginParams params) {
        OAuthLoginClient oAuthLoginClient = loginClients.get(params.registerRole());
        log.info(oAuthLoginClient.toString());
        return oAuthLoginClient.login(params);
    }

    @Transactional
    public LogoutResponse logout(Long userId, String accessToken) {

        if(ObjectUtils.isEmpty(accessToken)){
            throw GlobalException.CREDENTIALS_DO_NOT_EXIST;
        }

        if(!accessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            throw GlobalException.INVALID_HEADER;
        }

        accessToken = accessToken.substring(ACCESS_TOKEN_PREFIX.length());

        final String role = jwtTokenProvider.extractRole(accessToken); // MEMBER or TRAINER
        final String refresh_token_prefix = role.equals("MEMBER")? MEMBER_REFRESH_TOKEN_PREFIX:TRAINER_REFRESH_TOKEN_PREFIX;

        // Redis에서 해당 User id로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제

        if (ObjectUtils.isNotEmpty(redisClient.get(refresh_token_prefix + userId))){
            // Refresh Token을 삭제
            redisClient.delete(refresh_token_prefix + userId);
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList 저장
        final long expirationSeconds = (jwtTokenProvider.getExpiredDate(accessToken).getTime() - new Date().getTime()) / 1000;
        log.info("남은 시간 : {}" ,expirationSeconds);

        if(expirationSeconds > 0L) {
            redisClient.put(
                ACCESS_TOKEN_BLACK_LIST_PREFIX + accessToken,
                String.valueOf(userId),
                TimeUnit.SECONDS,
                expirationSeconds
            );
        }

        return LogoutResponse.builder()
            .message(LOGOUT_MESSAGE)
            .build();
    }

    public ReissueResponse reissue(String accessToken, String refreshToken) {

        accessToken = resolveTokenFromBearer(accessToken);

        // 1. Refresh Token 유효성 검사
        // 1-1. 유효하지 않거나 만료된 Refresh Token 일 시 Error Response
        // 리프레쉬까지 만료되면 재 로그인 =>  로그아웃 처리
        if (jwtTokenProvider.isExpiredToken(refreshToken)) {
            throw GlobalException.EXPIRED_REFRESH_TOKEN;
        }

        final Role role = Role.valueOf(jwtTokenProvider.extractRole(accessToken));
        final long userId = Long.parseLong(jwtTokenProvider.extractSubject(accessToken));

        // refresh token 검증
        final String refreshTokenPrefix = decideRefreshTokenPrefix(role);
        if (!redisClient.validationRefreshToken(refreshTokenPrefix + userId, refreshToken)) {
            throw GlobalException.INVALID_TOKEN;
        }

        // 2. Access Token 재발급
        ReissueResponse reissueResponse = ReissueResponse.of(
            authTokenGenerator.generateAccessToken(userId, role)
        );

        // 3. 현재시간과 Refresh Token의 만료일을 통해 남은 만료기간 계산
        long remainRefreshMilliseconds =
            (jwtTokenProvider.getExpiredDate(refreshToken).getTime() - new Date().getTime());

        // 4. Refresh Token의 남은 만료기간이 7일 / 3 미만일 시 Refresh Token도 재발급
        if (remainRefreshMilliseconds < REFRESH_TOKEN_VALID_TIME / 3) {
            TokenSetDto tokenSetDto = authTokenGenerator.generateTokenSet(userId, role);

            // 작성된 날짜에서 현재 날짜를 빼고 밀리초로 나누면 지나간 시간(초)이 계산
            long expirationSeconds = (
                jwtTokenProvider.getExpiredDate(tokenSetDto.getRefreshToken()).getTime() - new Date().getTime()
            ) / 1000;

            // redis에 refresh토큰 저장
            redisClient.put(
                refreshTokenPrefix + userId,
                tokenSetDto.getRefreshToken(),
                TimeUnit.SECONDS,
                expirationSeconds);

            reissueResponse = ReissueResponse.of(tokenSetDto);
        }

        return reissueResponse;
    }

    private String resolveTokenFromBearer(String accessToken) {
        if (ObjectUtils.isEmpty(accessToken)) {
            throw GlobalException.CREDENTIALS_DO_NOT_EXIST;
        }

        if (!accessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            throw GlobalException.INVALID_HEADER;
        }

        return accessToken.substring(ACCESS_TOKEN_PREFIX.length());
    }

    private String decideRefreshTokenPrefix(Role role) {
        return role.name().equals("MEMBER") ? MEMBER_REFRESH_TOKEN_PREFIX : TRAINER_REFRESH_TOKEN_PREFIX;
    }
}
