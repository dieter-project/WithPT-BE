package com.sideproject.withpt.application.pt.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class PTException extends GlobalException {


    public static final PTException MISSING_PT_DETAILS_INFO = new PTException(PTErrorCode.MISSING_PT_DETAILS_INFO);
    public static final PTException NO_REMAINING_PT = new PTException(PTErrorCode.NO_REMAINING_PT);
    public static final PTException PT_REGISTRATION_NOT_ALLOWED = new PTException(PTErrorCode.PT_REGISTRATION_NOT_ALLOWED);
    public static final PTException AlREADY_REGISTERED_PT_MEMBER = new PTException(PTErrorCode.AlREADY_REGISTERED_PT_MEMBER);
    public static final PTException PT_NOT_FOUND = new PTException(PTErrorCode.PT_NOT_FOUND);
    public static final PTException AlREADY_ALLOWED_PT_REGISTRATION = new PTException(PTErrorCode.AlREADY_ALLOWED_PT_REGISTRATION);
    public static final PTException AlREADY_REGISTERED_FIRST_PT_INFO = new PTException(PTErrorCode.AlREADY_REGISTERED_FIRST_PT_INFO);
    public static final PTException REMAINING_PT = new PTException(PTErrorCode.REMAINING_PT);
    public static final PTException INVALID_RE_REGISTRATION_DATE = new PTException(PTErrorCode.INVALID_RE_REGISTRATION_DATE);
    public static final PTException REMAINING_PT_CANNOT_EXCEED_THE_TOTAL_PT_NUMBER = new PTException(PTErrorCode.REMAINING_PT_CANNOT_EXCEED_THE_TOTAL_PT_NUMBER);

    private final PTErrorCode errorCode;

    public PTException(PTErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
