package com.sideproject.withpt.common.exception;

import com.sideproject.withpt.common.response.ApiErrorResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
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

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Object> handleConversionFailed(ConversionFailedException e, HttpServletRequest request) {
        log.warn("ConversionFailedException {}", e.getMessage());
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode, e.getMessage(), request);
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
        HttpStatus status, WebRequest request) {

        log.warn("TypeMismatchException {}", ex.getRootCause().getMessage());
        log.warn("TypeMismatchException {}", ex.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;

        String message = ex.getMessage();
        if(!ObjectUtils.isEmpty(ex.getRootCause())) {
            message = ex.getRootCause().getMessage();
        }

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiErrorResponse.builder()
                .status(errorCode.getHttpStatus().toString())
                .code(errorCode.name())
                .message(message)
                .requestUrl(((ServletWebRequest)request).getRequest().getRequestURI())
                .build());
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
        log.warn("Exception message {}", ex.getMessage());
        log.warn("Exception ", ex);
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
