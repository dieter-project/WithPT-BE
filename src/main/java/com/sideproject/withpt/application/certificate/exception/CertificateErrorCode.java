package com.sideproject.withpt.application.certificate.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CertificateErrorCode implements ErrorCode {

    DUPLICATE_CERTIFICATE(HttpStatus.BAD_REQUEST, "이미 동일한 자격증이 존재합니다."),
    CERTIFICATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 자격증이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
