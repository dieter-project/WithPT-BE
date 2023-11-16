package com.sideproject.withpt.application.education.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EducationErrorCode implements ErrorCode {

    DUPLICATE_EDUCATION(HttpStatus.BAD_REQUEST, "이미 동일한 교육 내역이 존재합니다."),
    EDUCATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 교육 내역이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
