package com.sideproject.withpt.application.pt.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PTErrorCode implements ErrorCode {

    AlREADY_REGISTERED_PT_MEMBER(HttpStatus.BAD_REQUEST, "이미 등록된 PT 회원입니다."),
    PT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 PT 정보는 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
