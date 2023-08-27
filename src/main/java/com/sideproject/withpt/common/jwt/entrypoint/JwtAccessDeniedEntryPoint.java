package com.sideproject.withpt.common.jwt.entrypoint;

import static com.sideproject.withpt.common.exception.CommonErrorCode.ACCESS_DENIED;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedEntryPoint implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", ACCESS_DENIED.getMessage());
        responseJson.put("status", ACCESS_DENIED.getHttpStatus());

        response.getWriter().print(responseJson);
    }

}
