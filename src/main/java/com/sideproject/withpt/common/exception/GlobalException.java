package com.sideproject.withpt.common.exception;

import lombok.Getter;

@Getter
//@RequiredArgsConstructor
public class GlobalException extends RuntimeException {

    public static final GlobalException TEST_ERROR = new GlobalException(CommonErrorCode.TEST_ERROR);
    public static final GlobalException ALREADY_REGISTERED_MEMBER = new GlobalException(CommonErrorCode.ALREADY_REGISTERED_MEMBER);
    public static final GlobalException INVALID_PARAMETER = new GlobalException(CommonErrorCode.INVALID_PARAMETER);

    private final ErrorCode errorCode;

    protected GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 의도적인 예외이므로 stack trace 제거(불필요한 예외처리 비용 제거)
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
