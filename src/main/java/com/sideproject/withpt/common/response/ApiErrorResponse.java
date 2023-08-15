package com.sideproject.withpt.common.response;

import com.sideproject.withpt.common.exception.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiErrorResponse {

    private final String status;
    private final String code;
    private final String message;
    private final String requestUrl;

    public static ApiErrorResponse from(ErrorCode errorCode, HttpServletRequest request) {
        return ApiErrorResponse.builder()
            .status(errorCode.getHttpStatus().toString())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .requestUrl(request.getRequestURI())
            .build();
    }

}
