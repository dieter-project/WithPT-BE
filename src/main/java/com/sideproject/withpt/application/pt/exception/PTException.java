package com.sideproject.withpt.application.pt.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class PTException extends GlobalException {

    public static final PTException AlREADY_REGISTERED_PT_MEMBER = new PTException(PTErrorCode.AlREADY_REGISTERED_PT_MEMBER);

    private final PTErrorCode errorCode;

    public PTException(PTErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
