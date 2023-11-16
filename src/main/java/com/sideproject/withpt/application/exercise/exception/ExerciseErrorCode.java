package com.sideproject.withpt.application.exercise.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExerciseErrorCode implements ErrorCode {

    EXERCISE_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 운동 데이터가 존재하지 않습니다."),
    BOOKMARK_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 북마크 데이터가 존재하지 않습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 북마크명입니다."),
    EXERCISE_NOT_BELONG_TO_MEMBER(HttpStatus.BAD_REQUEST, "작성한 회원과 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
