package com.sideproject.withpt.config;

import com.sideproject.withpt.common.jwt.JwtAuthenticationFilter;
import com.sideproject.withpt.common.jwt.JwtTokenProvider;
import com.sideproject.withpt.common.jwt.entrypoint.JwtAccessDeniedEntryPoint;
import com.sideproject.withpt.common.jwt.entrypoint.JwtAuthenticationEntryPoint;
import com.sideproject.withpt.common.redis.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisClient redisClient;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedEntryPoint jwtAccessDeniedEntryPoint;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers(
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 쿠키 기반이 아닌 JWT 기반이므로 사용하지 않음
            .csrf().disable()
            // JWT는 httpBearer방식이므로 httpBasic 비활성화
            .httpBasic().disable()
            // form login 안함
            .formLogin().disable()
            .cors()
            .and()
            // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 조건별로 요청 허용/제한 설정
            .authorizeRequests()
            // 회원가입과 로그인은 모두 승인
            .antMatchers(
                "/profile",
                "/index.html",
                "/api/v1/members/sign-up",
                "/api/v1/members/nickname/check",
                "/api/v1/trainers/sign-up",
                "/api/v1/oauth/google",
                "/api/v1/oauth/kakao").permitAll()
            // /trainers 로 시작하는 요청은 TRAINER 권한이 있는 유저에게만 허용
            .antMatchers("/api/v1/trainers/**").hasRole("TRAINER")
            // /members 로 시작하는 요청은 MEMBER 권한이 있는 유저에게만 허용
            .antMatchers("/api/v1/members/**").hasRole("MEMBER")
            .antMatchers("/api/v1/oauth/logout", "/api/v1/oauth/reissue").hasAnyRole("TRAINER", "MEMBER")
            .and()
            // JWT 인증 필터 적용
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisClient),
                UsernamePasswordAuthenticationFilter.class)
            // 에러 핸들링
            .exceptionHandling()
            .accessDeniedHandler(jwtAccessDeniedEntryPoint)
            .authenticationEntryPoint(jwtAuthenticationEntryPoint);

        return http.build();
    }
}
