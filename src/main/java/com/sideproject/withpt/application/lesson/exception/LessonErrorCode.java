package com.sideproject.withpt.application.lesson.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LessonErrorCode implements ErrorCode {

    ALREADY_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약된 수업입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
