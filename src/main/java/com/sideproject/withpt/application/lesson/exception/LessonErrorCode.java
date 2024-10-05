package com.sideproject.withpt.application.lesson.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LessonErrorCode implements ErrorCode {
    LESSON_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 수업입니다"),
    ALREADY_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약된 수업입니다."),
    NON_BOOKED_SESSION(HttpStatus.BAD_REQUEST, "예약 상태가 아닌 수업은 스케줄 변경이 불가능합니다."),
    ALREADY_PENDING_APPROVAL(HttpStatus.BAD_REQUEST, "승인 대기 중인 수업입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
