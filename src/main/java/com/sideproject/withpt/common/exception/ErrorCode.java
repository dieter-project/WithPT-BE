package com.sideproject.withpt.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TEST_ERROR(HttpStatus.BAD_REQUEST, "테스트 에러 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
