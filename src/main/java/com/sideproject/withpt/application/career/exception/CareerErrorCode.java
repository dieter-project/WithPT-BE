package com.sideproject.withpt.application.career.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CareerErrorCode implements ErrorCode {

    DUPLICATE_CAREER(HttpStatus.BAD_REQUEST, "이미 동일한 경력사항이 존재합니다."),
    CAREER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 경력 사항이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;

}
