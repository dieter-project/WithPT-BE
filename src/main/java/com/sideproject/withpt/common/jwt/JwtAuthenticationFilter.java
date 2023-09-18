package com.sideproject.withpt.common.jwt;

import static com.sideproject.withpt.common.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.sideproject.withpt.common.exception.CommonErrorCode.INVALID_TOKEN;
import static com.sideproject.withpt.common.exception.CommonErrorCode.NOT_VERIFICATION_LOGOUT;
import static com.sideproject.withpt.common.exception.CommonErrorCode.WRONG_TYPE_SIGNATURE;
import static com.sideproject.withpt.common.exception.CommonErrorCode.WRONG_TYPE_TOKEN;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TOKEN_HEADER;

import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.redis.RedisClient;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
//@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisClient redisClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        log.info("1. 권한이나 인증이 필요한 요청");
        log.info("CHECK JWT : JwtAuthenticationFilter.doFilterInternal");
        String token = this.resolveTokenFromRequest(request);

        try {

            if (StringUtils.hasText(token) && this.jwtTokenProvider.isValidationToken(token)) {

                if (ObjectUtils.isEmpty(redisClient.get(ACCESS_TOKEN_BLACK_LIST_PREFIX + token))) {

                    // 토큰이 유효하면 토큰으로부터 유저 정보를 받아온다
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    // SecurityContext 에 Authentication 객체를 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info(
                        String.format("[%s] -> %s", this.jwtTokenProvider.extractSubject(token),
                            request.getRequestURI())
                    );
                } else {
                    log.error("Access Black-List Token: {}", NOT_VERIFICATION_LOGOUT.getMessage());
                    request.setAttribute("exception", NOT_VERIFICATION_LOGOUT.getMessage());
                }
            }// 에러가 발생했을 때, request에 attribute를 세팅하고 RestAuthenticationEntryPoint로 request를 넘겨준다.
        } catch (GlobalException e) { // 유효한 헤더가 입력되지 않았음. Bearer
            log.error("JWT header is invalid: {}", e.getMessage());
            request.setAttribute("exception", e.getMessage());
        } catch (ExpiredJwtException e) { // "만료된 JWT 토큰입니다."
            log.error("JWT token is expired: {}", e.getMessage());
            request.setAttribute("exception", EXPIRED_ACCESS_TOKEN.getMessage());
        } catch (SignatureException e) { // "잘못된 JWT 서명입니다."
            log.error("Invalid JWT signature: {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_SIGNATURE.getMessage());
        } catch (SecurityException | MalformedJwtException e) { // "유효하지 않은 구성의 JWT 토큰입니다.
            log.info("Invalid JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) { // "지원되지 않는 형식이나 구성의 JWT 토큰입니다."
            log.info("Unsupported JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Illegal Argument Exception {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {"/profile",
            "/index.html",
            "/api/v1/members/sign-up",
            "/api/v1/trainers/sign-up",
            "/api/v1/members/nickname/check",
            "/api/v1/oauth/google",
            "/api/v1/oauth/kakao"};
        
        return Arrays.stream(excludePath).anyMatch(request.getRequestURI()::startsWith);
    }

    // request에 있는 header로부터 token 얻기
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        log.info("jwt header : {}", token);
        log.info("===============================================================\n");

        log.info("2. Header 확인");
        validationHeader(token);

        return token.substring(ACCESS_TOKEN_PREFIX.length());
    }

    private void validationHeader(String token) {
        if (ObjectUtils.isEmpty(token)) {
            throw GlobalException.CREDENTIALS_DO_NOT_EXIST;
        }

        if (!token.startsWith(ACCESS_TOKEN_PREFIX)) {
            throw GlobalException.INVALID_HEADER;
        }
    }
}
