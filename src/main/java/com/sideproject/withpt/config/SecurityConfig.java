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
import org.springframework.http.HttpMethod;
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
            .csrf().disable() // 쿠키 기반이 아닌 JWT 기반이므로 사용하지 않음
            .httpBasic().disable() // JWT는 httpBearer방식이므로 httpBasic 비활성화
            .formLogin().disable() // form login 안함
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
            .and()
            .authorizeRequests() // 조건별로 요청 허용/제한 설정
            .antMatchers(
                "/profile",
                "/index.html",
                "/api/v1/members/sign-up",
                "/api/v1/members/nickname/check",
                "/api/v1/trainers/sign-up",
                "/api/v1/oauth/google",
                "/api/v1/oauth/kakao",
                "/api/v1/auth/login").permitAll() // 회원가입과 로그인은 모두 승인
            .antMatchers("/ws-stomp/**", "/topic/**", "/exchange/**", "/pub/**").permitAll()
            .antMatchers("/api/v1/oauth/logout", "/api/v1/oauth/reissue").hasAnyRole("TRAINER", "MEMBER")
            .antMatchers(HttpMethod.GET,
                "/api/v1/trainers/{trainerId}/info",
                "/api/v1/trainers/{trainerId}/careers", // 경력
                "/api/v1/trainers/{trainerId}/certificates", // 자격증
                "/api/v1/trainers/{trainerId}/awards", // 수상
                "/api/v1/trainers/{trainerId}/educations", // 교육
                "/api/v1/trainers/{trainerId}/academics" // 학력
            ).hasAnyRole("TRAINER", "MEMBER")
            .antMatchers("/api/v1/trainers/**", "/api/v1/members/search").hasRole("TRAINER") // /trainers 로 시작하는 요청은 TRAINER 권한이 있는 유저에게만 허용
            .antMatchers("/api/v1/members/**").hasRole("MEMBER") // /members 로 시작하는 요청은 MEMBER 권한이 있는 유저에게만 허용
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisClient), UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 적용
            .exceptionHandling()// 에러 핸들링
            .accessDeniedHandler(jwtAccessDeniedEntryPoint)
            .authenticationEntryPoint(jwtAuthenticationEntryPoint);

        return http.build();
    }
}
