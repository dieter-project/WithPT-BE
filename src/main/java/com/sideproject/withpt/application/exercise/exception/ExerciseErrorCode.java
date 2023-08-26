package com.sideproject.withpt.application.exercise.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExerciseErrorCode implements ErrorCode {

    EXERCISE_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 운동 데이터가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
