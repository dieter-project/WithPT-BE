package com.sideproject.withpt.application.body.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BodyErrorCode implements ErrorCode {

    WEIGHT_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 체중 기록 데이터가 존재하지 않습니다."),
    WEIGHT_NOT_BELONG_TO_MEMBER(HttpStatus.BAD_REQUEST, "작성한 회원과 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
