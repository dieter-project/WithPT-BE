package com.sideproject.withpt.common.exception;

import com.sideproject.withpt.common.response.ApiErrorResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<Object> handleGlobalException(GlobalException e, HttpServletRequest request) {
        log.warn("handleGlobalException {}", e.getMessage());
        return handleExceptionInternal(e.getErrorCode(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("handleIllegalArgument {}", e.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        log.warn("handleMethodArgumentNotValid {}", ex.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(ex, errorCode, request);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        log.warn("Exception {}", ex.getMessage());
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.from(ex, errorCode, request));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.from(errorCode, request));
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode, WebRequest request) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.from(e, errorCode, request));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message,
        HttpServletRequest request) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.from(errorCode, message, request));
    }


}
