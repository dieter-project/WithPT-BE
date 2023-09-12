package com.sideproject.withpt.common.jwt.entrypoint;

import static com.sideproject.withpt.common.exception.CommonErrorCode.CREDENTIALS_DO_NOT_EXIST;
import static com.sideproject.withpt.common.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.sideproject.withpt.common.exception.CommonErrorCode.INVALID_HEADER;
import static com.sideproject.withpt.common.exception.CommonErrorCode.INVALID_TOKEN;
import static com.sideproject.withpt.common.exception.CommonErrorCode.NOT_VERIFICATION_LOGOUT;
import static com.sideproject.withpt.common.exception.CommonErrorCode.USER_NOT_FOUND;
import static com.sideproject.withpt.common.exception.CommonErrorCode.WRONG_TYPE_SIGNATURE;
import static com.sideproject.withpt.common.exception.CommonErrorCode.WRONG_TYPE_TOKEN;

import com.sideproject.withpt.common.exception.ErrorCode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute("exception");

        if (exception.equals(CREDENTIALS_DO_NOT_EXIST.getMessage())) {
            log.info("JwtAuthenticationEntryPoint : " + CREDENTIALS_DO_NOT_EXIST);
            setResponse(response, CREDENTIALS_DO_NOT_EXIST);
        } else if (exception.equals(INVALID_HEADER.getMessage())) {
            log.info("JwtAuthenticationEntryPoint : " + INVALID_HEADER);
            setResponse(response, INVALID_HEADER);
        } else if (exception.equals(NOT_VERIFICATION_LOGOUT.getMessage())) { // 로그아웃된 토큰
            log.info("JwtAuthenticationEntryPoint : " + NOT_VERIFICATION_LOGOUT);
            setResponse(response, NOT_VERIFICATION_LOGOUT);
        } else if (exception.equals(EXPIRED_ACCESS_TOKEN.getMessage())) { // 만료된 JWT 토큰입니다."
            log.info("JwtAuthenticationEntryPoint : " + EXPIRED_ACCESS_TOKEN);
            setResponse(response, EXPIRED_ACCESS_TOKEN);
        } else if (exception.equals(INVALID_TOKEN.getMessage())) {
            log.info("JwtAuthenticationEntryPoint : " + INVALID_TOKEN);
            setResponse(response, INVALID_TOKEN);
        } else if (exception.equals(WRONG_TYPE_SIGNATURE.getMessage())) { // "잘못된 JWT 서명입니다."
            log.info("JwtAuthenticationEntryPoint : " + WRONG_TYPE_SIGNATURE);
            setResponse(response, WRONG_TYPE_SIGNATURE);
        } else if (exception.equals(
            WRONG_TYPE_TOKEN.getMessage())) { // "지원되지 않는 형식이나 구성의 JWT 토큰입니다." // "유효하지 않은 구성의 JWT 토큰입니다.
            log.info("JwtAuthenticationEntryPoint : " + WRONG_TYPE_TOKEN);
            setResponse(response, WRONG_TYPE_SIGNATURE);
        } else{
            log.info("JwtAuthenticationEntryPoint : {}",USER_NOT_FOUND);
            setResponse(response, USER_NOT_FOUND);
        }
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", errorCode.getMessage());
        responseJson.put("status", errorCode.getHttpStatus());

        response.getWriter().print(responseJson);
    }
}
