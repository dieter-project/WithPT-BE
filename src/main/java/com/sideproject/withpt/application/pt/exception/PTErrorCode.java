package com.sideproject.withpt.application.pt.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PTErrorCode implements ErrorCode {

    MAX_QUERY_MONTHS(HttpStatus.BAD_REQUEST, "최대 12개월까지 조회 가능합니다"),
    MISSING_PT_DETAILS_INFO(HttpStatus.BAD_REQUEST, "PT 상세 정보를 입력하지 않으셨습니다."),
    NO_REMAINING_PT(HttpStatus.BAD_REQUEST,"잔여 PT 횟수가 남아 있지 않습니다."),
    PT_REGISTRATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "아직 PT 등록을 허용하지 않은 회원입니다."),
    AlREADY_REGISTERED_FIRST_PT_INFO(HttpStatus.BAD_REQUEST, "이미 초기 PT 정보가 등록되어 있습니다."),
    AlREADY_ALLOWED_PT_REGISTRATION(HttpStatus.BAD_REQUEST, "이미 PT 등록을 허용한 상태입니다."),
    AlREADY_REGISTERED_PT_MEMBER(HttpStatus.BAD_REQUEST, "이미 등록된 PT 회원입니다."),
    REMAINING_PT(HttpStatus.BAD_REQUEST, "잔여 PT 횟수가 남아 있습니다. 정말 해제하시겠습니까?"),
    PT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 PT 정보는 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
