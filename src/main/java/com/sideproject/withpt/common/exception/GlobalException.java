package com.sideproject.withpt.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GlobalException extends RuntimeException {

    public static final GlobalException TEST_ERROR = new GlobalException(ErrorCode.TEST_ERROR);
    public static final GlobalException DUPLICATE_NICKNAME = new GlobalException(ErrorCode.DUPLICATE_NICKNAME);
    public static final GlobalException ALREADY_REGISTERED_MEMBER = new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER);
    public static final GlobalException INVALID_PARAMETER = new GlobalException(ErrorCode.INVALID_PARAMETER);

    private final ErrorCode errorCode;

    // 의도적인 예외이므로 stack trace 제거(불필요한 예외처리 비용 제거)
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
