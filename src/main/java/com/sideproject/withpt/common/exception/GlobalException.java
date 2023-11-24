package com.sideproject.withpt.common.exception;

import lombok.Getter;

@Getter
//@RequiredArgsConstructor
public class GlobalException extends RuntimeException {

    public static final GlobalException TEST_ERROR = new GlobalException(CommonErrorCode.TEST_ERROR);
    public static final GlobalException ALREADY_REGISTERED_USER = new GlobalException(CommonErrorCode.ALREADY_REGISTERED_USER);
    public static final GlobalException INVALID_PARAMETER = new GlobalException(CommonErrorCode.INVALID_PARAMETER);
    public static final GlobalException USER_NOT_FOUND = new GlobalException(CommonErrorCode.USER_NOT_FOUND);
    public static final GlobalException INVALID_HEADER = new GlobalException(CommonErrorCode.INVALID_HEADER);
    public static final GlobalException CREDENTIALS_DO_NOT_EXIST = new GlobalException(CommonErrorCode.CREDENTIALS_DO_NOT_EXIST);
    public static final GlobalException REDIS_PUT_EMPTY_KEY = new GlobalException(CommonErrorCode.REDIS_PUT_EMPTY_KEY);
    public static final GlobalException REDIS_PUT_FAIL = new GlobalException(CommonErrorCode.REDIS_PUT_FAIL);
    public static final GlobalException EXPIRED_REFRESH_TOKEN = new GlobalException(CommonErrorCode.EXPIRED_REFRESH_TOKEN);
    public static final GlobalException INVALID_TOKEN = new GlobalException(CommonErrorCode.INVALID_TOKEN);
    public static final GlobalException EMPTY_FILE = new GlobalException(CommonErrorCode.EMPTY_FILE);
    public static final GlobalException FILE_UPLOAD_FAILED = new GlobalException(CommonErrorCode.FILE_UPLOAD_FAILED);
    public static final GlobalException FILE_DELETE_FAILED = new GlobalException(CommonErrorCode.FILE_DELETE_FAILED);
    public static final GlobalException EMPTY_DELETE_FILE = new GlobalException(CommonErrorCode.EMPTY_DELETE_FILE);

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
