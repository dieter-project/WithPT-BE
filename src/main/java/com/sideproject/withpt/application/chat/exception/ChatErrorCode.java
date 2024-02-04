package com.sideproject.withpt.application.chat.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 생성 중 오류가 발생했습니다"),

    INVALID_USER_IDENTIFIER(HttpStatus.BAD_REQUEST, "로그인한 유저의 식별자 값이 잘못되었습니다"),
    INVALID_REQUESTED_CHAT_IDENTIFIER(HttpStatus.BAD_REQUEST, "채팅을 요청받은 유저의 식별자 값이 잘못되었습니다"),
    CHAT_ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "채팅 방이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
