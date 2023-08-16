package com.sideproject.withpt.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sideproject.withpt.common.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiErrorResponse {

    private final String status;
    private final String code;
    private final String message;
    private final String requestUrl;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError{

        private String field;
        private String message;

        public static ValidationError of(final FieldError fieldError){
            return ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
        }
    }

    public static ApiErrorResponse from(ErrorCode errorCode, HttpServletRequest request) {
        return ApiErrorResponse.builder()
            .status(errorCode.getHttpStatus().toString())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .requestUrl(request.getRequestURI())
            .build();
    }

    public static ApiErrorResponse from(BindException e, ErrorCode errorCode, WebRequest request) {
        List<ApiErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(ApiErrorResponse.ValidationError::of)
            .collect(Collectors.toList());

        return ApiErrorResponse.builder()
            .status(errorCode.getHttpStatus().toString())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .errors(validationErrorList)
            .requestUrl(((ServletWebRequest)request).getRequest().getRequestURI())
            .build();
    }

    public static ApiErrorResponse from(ErrorCode errorCode, String message, HttpServletRequest request) {
        return ApiErrorResponse.builder()
            .status(errorCode.getHttpStatus().toString())
            .code(errorCode.name())
            .message(message)
            .requestUrl(request.getRequestURI())
            .build();
    }

}
