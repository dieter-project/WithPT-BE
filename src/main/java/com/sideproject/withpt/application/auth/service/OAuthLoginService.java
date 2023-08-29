package com.sideproject.withpt.application.auth.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TRAINER_REFRESH_TOKEN_PREFIX;

import com.sideproject.withpt.application.auth.controller.dto.LogoutResponse;
import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.OAuthLoginParams;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.JwtTokenProvider;
import com.sideproject.withpt.common.redis.RedisClient;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final RedisClient redisClient;

    private static final String LOGOUT_MESSAGE = "로그아웃되셨습니다.";

    public OAuthLoginService(List<OAuthLoginClient> clients, JwtTokenProvider jwtTokenProvider, RedisClient redisClient) {
        this.loginClients = clients.stream().collect(
            Collectors.toUnmodifiableMap(OAuthLoginClient::role, Function.identity())
        );

        this.jwtTokenProvider = jwtTokenProvider;
        this.redisClient = redisClient;
    }

    @Transactional
    public OAuthLoginResponse login(OAuthLoginParams params) {
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

}
