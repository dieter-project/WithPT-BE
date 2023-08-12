package com.sideproject.withpt.common.exception;

import com.sideproject.withpt.common.response.ApiErrorResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ApiErrorResponse> handleGlobalException(GlobalException e, HttpServletRequest request) {
        return handleExceptionInternal(e.getErrorCode(), request);
    }

    private ResponseEntity<ApiErrorResponse> handleExceptionInternal(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.from(errorCode, request));
    }

}
