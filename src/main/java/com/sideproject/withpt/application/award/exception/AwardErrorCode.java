package com.sideproject.withpt.application.award.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AwardErrorCode implements ErrorCode {

    DUPLICATE_AWARD(HttpStatus.BAD_REQUEST, "이미 동일한 수상 내역이 존재합니다."),
    AWARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 수상 내역이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
