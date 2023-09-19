package com.sideproject.withpt.application.gym.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GymErrorCode implements ErrorCode {

    GYM_TRAINER_NOT_MAPPING(HttpStatus.BAD_REQUEST, "해당 트레이너가 소속된 체육관이 존재하지 않습니다."),
    GYM_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 체육관 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
