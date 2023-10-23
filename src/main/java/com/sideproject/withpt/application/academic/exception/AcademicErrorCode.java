package com.sideproject.withpt.application.academic.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AcademicErrorCode implements ErrorCode {

    DUPLICATE_ACADEMIC(HttpStatus.BAD_REQUEST, "이미 동일한 학력사항이 존재합니다."),
    ACADEMIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 학력 사항이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
