package com.sideproject.withpt.application.gymtrainer.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GymTrainerErrorCode implements ErrorCode {

    GYM_TRAINER_NOT_MAPPING(HttpStatus.BAD_REQUEST, "트레이너가 소속된 체육관이 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
