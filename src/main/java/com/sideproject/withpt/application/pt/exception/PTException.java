package com.sideproject.withpt.application.pt.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class PTException extends GlobalException {

    public static final PTException AlREADY_REGISTERED_PT_MEMBER = new PTException(PTErrorCode.AlREADY_REGISTERED_PT_MEMBER);
    public static final PTException PT_NOT_FOUND = new PTException(PTErrorCode.PT_NOT_FOUND);
    public static final PTException AlREADY_ALLOWED_PT_REGISTRATION = new PTException(PTErrorCode.AlREADY_ALLOWED_PT_REGISTRATION);
    public static final PTException AlREADY_REGISTERED_FIRST_PT_INFO = new PTException(PTErrorCode.AlREADY_REGISTERED_FIRST_PT_INFO);

    private final PTErrorCode errorCode;

    public PTException(PTErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
