package com.sideproject.withpt.application.record.diet.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DietErrorCode implements ErrorCode {

    DIET_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 식단 데이터가 존재하지 않습니다."),
    DIET_FOOD_NOT_EXIST(HttpStatus.BAD_REQUEST, "식단 음식 데이터가 존재하지 않습니다."),
    AT_LEAST_ONE_DIET_DATA_MUST_BE_INCLUDED(HttpStatus.BAD_REQUEST, "최소 하나 이상의 식단 음식 데이터가 포함되어야 합니다."),
    DIET_NOT_BELONG_TO_MEMBER(HttpStatus.BAD_REQUEST, "작성한 회원과 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
