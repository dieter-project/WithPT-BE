package com.sideproject.withpt.application.auth.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.password.PasswordLoginParams;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.member.Member;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MemberOAuthLoginClientTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberOAuthLoginClient memberOAuthLoginClient;

    @MockBean
    private RedisClient redisClient;

    @DisplayName("이메일 - 패스워드 기반 로그인")
    @Test
    void loginByPassword() {
        // given
        String email = "test@test.com";
        String password = "test1234";

        Member member = Member.builder()
            .email(email)
            .password(password)
            .role(Role.MEMBER)
            .authProvider(AuthProvider.EMAIL)
            .build();
        memberRepository.save(member);

        willDoNothing()
            .given(redisClient)
            .put(anyString(), anyString(), any(TimeUnit.class), anyLong());

        PasswordLoginParams request = PasswordLoginParams.builder()
            .email(email)
            .password("test1234")
            .role(Role.MEMBER)
            .build();

        // when
        OAuthLoginResponse response = memberOAuthLoginClient.login(request);

        // then
        assertThat(response)
            .extracting("email", "authProvider", "role")
            .contains(email, AuthProvider.EMAIL, Role.MEMBER);
    }

    @DisplayName("이메일 - 패스워드 기반 로그인")
    @Test
    void loginWhenInvalidPassword() {
        // given
        String email = "test@test.com";

        Member member = Member.builder()
            .email(email)
            .password("test1234")
            .role(Role.MEMBER)
            .authProvider(AuthProvider.EMAIL)
            .build();
        memberRepository.save(member);

        willDoNothing()
            .given(redisClient)
            .put(anyString(), anyString(), any(TimeUnit.class), anyLong());

        PasswordLoginParams request = PasswordLoginParams.builder()
            .email(email)
            .password("test123")
            .role(Role.MEMBER)
            .build();

        // when // then
        assertThatThrownBy(() -> memberOAuthLoginClient.login(request))
            .isInstanceOf(GlobalException.class)
            .hasMessage("Invalid password");
    }


}