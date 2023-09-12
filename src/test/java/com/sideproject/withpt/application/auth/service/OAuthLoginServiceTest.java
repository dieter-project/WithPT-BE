package com.sideproject.withpt.application.auth.service;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_VALID_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sideproject.withpt.application.auth.controller.dto.LogoutResponse;
import com.sideproject.withpt.application.auth.controller.dto.ReissueResponse;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.JwtTokenProvider;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    @Mock
    List<OAuthLoginClient> clients;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthTokenGenerator authTokenGenerator;

    @Mock
    RedisClient redisClient;

    @InjectMocks
    OAuthLoginService oAuthLoginService;

    private static final String ACCESS_TOKEN_PREFIX = "Bearer ";
    private static final String MEMBER_REFRESH_TOKEN_PREFIX = "Refresh:MEMBER";
    private static final String TRAINER_REFRESH_TOKEN_PREFIX = "TRAINER_PREFIX_";
    private static final String ACCESS_TOKEN_BLACK_LIST_PREFIX = "BLACKLIST_";

    private static final String LOGOUT_MESSAGE = "로그아웃되셨습니다.";

    @Test
    public void logout_success() {
        //given
        Long userId = 123L;
        String accessToken = "Bearer test_access_token";
        String role = "MEMBER";
        String refreshTokenKey = MEMBER_REFRESH_TOKEN_PREFIX + userId;

        given(jwtTokenProvider.extractRole(accessToken.substring(ACCESS_TOKEN_PREFIX.length())))
            .willReturn(role);
        given(redisClient.get(refreshTokenKey)).willReturn("test_refresh_token");
        // 현재 시간보다 미래 시간이면 accesstoken 만료 시간이 남은것
        given(jwtTokenProvider.getExpiredDate(accessToken.substring(ACCESS_TOKEN_PREFIX.length())))
            .willReturn(new Date(System.currentTimeMillis() + 10000));

        //when
        LogoutResponse response = oAuthLoginService.logout(userId, accessToken);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(LOGOUT_MESSAGE);
        verify(redisClient, times(1)).delete(refreshTokenKey);
    }

    @Test
    public void logout_empty_token() {

        Long userId = 123L;
        String accessToken = null;

        assertThatThrownBy(
            () -> oAuthLoginService.logout(userId, accessToken)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.CREDENTIALS_DO_NOT_EXIST.getMessage());
    }

    @Test
    public void logout_token_header_invalid () {
        //given
        Long userId = 123L;
        String accessToken = "Bear access";

        assertThatThrownBy(
            () -> oAuthLoginService.logout(userId, accessToken)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.INVALID_HEADER.getMessage());
    }

    @Test
    public void reissue_only_accessToken () {
        //given
        String accessToken ="test_access_token";
        String refreshToken = "test_refresh_token";
        Role role = Role.MEMBER;
        long userId = 123L;

        given(jwtTokenProvider.extractRole(accessToken))
            .willReturn(String.valueOf(role));
        given(jwtTokenProvider.extractSubject(accessToken))
            .willReturn(String.valueOf(userId));
        given(redisClient.validationRefreshToken(MEMBER_REFRESH_TOKEN_PREFIX + userId, refreshToken))
            .willReturn(true);
        given(authTokenGenerator.generateAccessToken(userId, role))
            .willReturn(TokenSetDto.builder()
                .accessToken(accessToken)
                .accessExpiredAt(1800L)
                .build());
        given(jwtTokenProvider.getExpiredDate(refreshToken))
            .willReturn(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME) );

        //when
        ReissueResponse reissueResponse = oAuthLoginService.reissue(ACCESS_TOKEN_PREFIX + accessToken, refreshToken);

        //then
        assertThat(reissueResponse.getAccessToken()).isEqualTo(accessToken);
        assertThat(reissueResponse.getAccessExpiredAt()).isEqualTo(1800L);
    }

    @Test
    public void reissue_refresh_and_accessToken () {
        //given
        String accessToken ="test_access_token";
        String refreshToken = "test_refresh_token";
        Role role = Role.MEMBER;
        long userId = 123L;

        given(jwtTokenProvider.isExpiredToken(refreshToken))
            .willReturn(false);
        given(jwtTokenProvider.extractRole(accessToken))
            .willReturn(String.valueOf(role));
        given(jwtTokenProvider.extractSubject(accessToken))
            .willReturn(String.valueOf(userId));
        given(redisClient.validationRefreshToken(MEMBER_REFRESH_TOKEN_PREFIX + userId, refreshToken))
            .willReturn(true);
        given(authTokenGenerator.generateAccessToken(userId, role))
            .willReturn(TokenSetDto.builder()
                .accessToken(accessToken)
                .accessExpiredAt(1800L)
                .build());
        given(jwtTokenProvider.getExpiredDate(refreshToken))
            .willReturn(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME / 3));
        given(authTokenGenerator.generateTokenSet(userId, role))
            .willReturn(TokenSetDto.builder()
                .accessToken(accessToken)
                .accessExpiredAt(1800L)
                .refreshToken(refreshToken)
                .refreshExpiredAt(7600L)
                .build());

        //when
        ReissueResponse reissueResponse = oAuthLoginService.reissue(ACCESS_TOKEN_PREFIX + accessToken, refreshToken);

        //then
        assertThat(reissueResponse.getAccessToken()).isEqualTo(accessToken);
        assertThat(reissueResponse.getAccessExpiredAt()).isEqualTo(1800L);
        assertThat(reissueResponse.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(reissueResponse.getRefreshExpiredAt()).isEqualTo(7600L);
    }

    @Test
    public void refresh_expired () {
        //given
        String accessToken = "Bearer test_access";
        String refreshToken = "test_refresh";

        given(jwtTokenProvider.isExpiredToken(refreshToken))
            .willReturn(true);

        assertThatThrownBy(
            () -> oAuthLoginService.reissue(accessToken, refreshToken)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @Test
    public void redis_validation_refresh () {
        //given
        String accessToken = "Bearer test_access";
        String refreshToken = "test_refresh";
        Role role = Role.MEMBER;
        long userId = 123L;

        given(jwtTokenProvider.isExpiredToken(refreshToken))
            .willReturn(false);
        given(jwtTokenProvider.extractRole(accessToken.substring(ACCESS_TOKEN_PREFIX.length())))
            .willReturn(String.valueOf(role));
        given(jwtTokenProvider.extractSubject(accessToken.substring(ACCESS_TOKEN_PREFIX.length())))
            .willReturn(String.valueOf(userId));

        given(redisClient.validationRefreshToken(MEMBER_REFRESH_TOKEN_PREFIX + userId, refreshToken))
            .willReturn(false);

        assertThatThrownBy(
            () -> oAuthLoginService.reissue(accessToken, refreshToken)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.INVALID_TOKEN.getMessage());
    }
}